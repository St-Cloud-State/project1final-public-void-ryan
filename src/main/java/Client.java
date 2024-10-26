import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String CLIENT_STRING = "C";
    private static int idCounter = 1;
    private final String id;
    private String name;
    private String address;
    private String phone;
    private double balance;
    private final Wishlist wishlist;
    private final TransactionList transactions;

    public Client(String name, String address, String phone) {
        this.id = CLIENT_STRING + idCounter++;
        this.wishlist = new Wishlist();
        this.transactions = new TransactionList();
        setName(name);
        setAddress(address);
        setPhone(phone);
        setBalance(0);
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getBalance() {
        return balance;
    }

    public String getId() {
        return id;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    // Setters
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty.");
        }
        this.phone = phone;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return String.format("Client [ID=%s, Name=%s, Address=%s, Phone=%s, Balance=%.2f]", id, name, address, phone,
                balance);
    }

    public Wishlist.WishlistItem addToWishlist(Product product, int quantity) {
        return wishlist.addProduct(product, quantity);
    }

    public boolean removeFromWishlist(Product product) {
        return wishlist.removeProduct(product);
    }

    public void clearWishlist() {
        wishlist.clear();
    }

    public String addTransaction(String content) {
        return transactions.addTransaction(content);
    }

    public Iterator<TransactionList.TransactionItem> getTransactions() {
        return transactions.getTransactions();
    }

    public String getTransaction(String transactionId) {
        return transactions.getTransaction(transactionId);
    }
}
