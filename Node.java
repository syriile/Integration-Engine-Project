public class Node<T extends Comparable<T>> implements Comparable<Node<T>>{
    // -------------------------------------------------------------
    // Data Fields
    // -------------------------------------------------------------
    private T data;
    private Node<T> left;
    private Node<T> right;

    // -------------------------------------------------------------
    // Constructor & Overloaded Constructor
    // -------------------------------------------------------------
    public Node() {
        this.data = null;
        this.left = null;
        this.right = null;
    }
    public Node(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    // -------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------
    public T getData() {return data;}
    public Node<T> getLeft() {return left;}
    public Node<T> getRight() {return right;}
    
    // -------------------------------------------------------------
    // Mutators
    // -------------------------------------------------------------
    public void setData(T data) {this.data = data;}
    public void setLeft(Node<T> left) {this.left = left;}
    public void setRight(Node<T> right) {this.right = right;}

    // -------------------------------------------------------------
    // compareTo method
    // -------------------------------------------------------------
    @Override
    public int compareTo(Node<T> node2) {
        if(this.data == null && node2.data == null) return 0;
        if(this.data == null) return -1;
        if(node2.data == null) return 1;
        return this.data.compareTo(node2.data);
    }    
}
