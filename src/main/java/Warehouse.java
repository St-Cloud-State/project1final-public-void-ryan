import java.io.*;
import java.util.*;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Warehouse warehouse;
    private final ClientList clients;
    private final ProductList products;

    private static final String DATA_FILE = "WarehouseData";

    private Warehouse() {
        clients = ClientList.instance();
        products = ProductList.instance();
    }

    public static Warehouse instance() {
        if (warehouse == null) {
            warehouse = retrieve();
            if (warehouse == null) {
                warehouse = new Warehouse();
            }
        }
        return warehouse;
    }

    public static void resetInstance() {
        warehouse = null;
    }

    public Client addClient(String name, String address, String phone) {
        Client client = new Client(name, address, phone);
        return clients.addClient(client);
    }

    public Product addProduct(String name, double price, int quantity) {
        Product product = new Product(name, price, quantity);
        return products.addProduct(product);
    }

    public Wishlist.WishlistItem addProductToClientWishlist(String clientId, String productId, int wishlistQuantity) {
        Client client = clients.findClient(clientId);
        Product product = products.findProduct(productId);
        return client.addToWishlist(product, wishlistQuantity);
    }

    public void removeProductFromClientWishlist(String clientId, String productId) {
        Client client = clients.findClient(clientId);
        Product product = products.findProduct(productId);
        client.removeFromWishlist(product);
    }

    public void clearClientWishlist(String clientId) {
        Client client = clients.findClient(clientId);
        client.clearWishlist();
    }

    public void processClientOrder(String clientId, String productId, int orderQuantity) {
        Client client = clients.findClient(clientId);
        Product product = products.findProduct(productId);

        int stockLevel = product.getStockLevel();
        double productPrice = product.getPrice();

        if (stockLevel >= orderQuantity) {
            // Enough stock to fulfill the entire order
            product.setStockLevel(stockLevel - orderQuantity);
            double totalCost = productPrice * orderQuantity;
            client.setBalance(client.getBalance() + totalCost);
        } else if (stockLevel > 0) {
            // Partial fulfillment
            int waitlistedQuantity = orderQuantity - stockLevel;
            product.setStockLevel(0);
            double fulfilledCost = productPrice * stockLevel;
            client.setBalance(client.getBalance() + fulfilledCost);

            // Waitlist the remaining quantity
            Waitlist waitlist = product.getWaitlist();
            waitlist.addClient(client, waitlistedQuantity);
        } else {
            // No stock available, entire quantity goes to waitlist
            Waitlist waitlist = product.getWaitlist();
            waitlist.addClient(client, orderQuantity);
        }
    }

    public void processClientPayment(String clientId, double paymentAmount) {
        Client client = clients.findClient(clientId);
        double newBalance = client.getBalance() - paymentAmount;
        client.setBalance(newBalance);
    }

    public void processProductShipment(String productId, int shipmentQuantity) {
        Product product = products.findProduct(productId);
        Iterator<Waitlist.WaitlistItem> waitlistItems = product.getWaitlist().getWaitlistItems();

        // Update the stock quantity before processing all the waitlist items
        product.setStockLevel(product.getStockLevel() + shipmentQuantity);

        while (waitlistItems.hasNext()) {
            Waitlist.WaitlistItem waitlistItem = waitlistItems.next();
            String waitlistClientId = waitlistItem.getClient().getId();
            int waitlistQuantity = waitlistItem.getQuantity();
            waitlistItems.remove();

            processClientOrder(waitlistClientId, productId, waitlistQuantity);
        }
    }

    public Client getClient(String clientId) {
        return clients.findClient(clientId);
    }

    public Iterator<Client> getClients() {
        return clients.getClients();
    }

    public Product getProduct(String productId) {
        return products.findProduct(productId);
    }

    public Iterator<Product> getProducts() {
        return products.getProducts();
    }

    public Iterator<Wishlist.WishlistItem> getClientWishlistItems(String clientId) {
        Client client = clients.findClient(clientId);
        return client.getWishlist().getWishlistItems();
    }

    public Iterator<Waitlist.WaitlistItem> getProductWaitlistItems(String productId) {
        Product product = products.findProduct(productId);
        return product.getWaitlist().getWaitlistItems();
    }

    public static boolean save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(warehouse);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Warehouse retrieve() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            warehouse = (Warehouse) in.readObject();
            return warehouse;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
