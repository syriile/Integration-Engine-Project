public class BinTree<T extends Comparable<T>> {
    // -------------------------------------------------------------
    // Data Fields
    // -------------------------------------------------------------
    private Node<T> root;

    // -------------------------------------------------------------
    // Constructor & Overloaded Constructor
    // -------------------------------------------------------------
    public BinTree() {this.root = null;}
    public BinTree(T data) {root = new Node<>(data);}


    // -------------------------------------------------------------
    // Accessor
    // -------------------------------------------------------------
    public Node<T> getRoot() {return root;}

    // -------------------------------------------------------------
    // Recursive Insert Method
    // -------------------------------------------------------------
    public void insert(T data) {root = recursiveInsert(root, data);}
    private Node<T> recursiveInsert(Node<T> cur, T data) {
        if(cur == null)
            return new Node<>(data);

        if(data.compareTo(cur.getData()) < 0) 
            cur.setLeft(recursiveInsert(cur.getLeft(), data));
        else if (data.compareTo(cur.getData()) > 0) 
            cur.setRight(recursiveInsert(cur.getRight(), data));
        
        return cur;
    }

    // -------------------------------------------------------------
    // Recursive Search Method
    // -------------------------------------------------------------
    public T search(T target) {return recursiveSearch(root, target);}
    private T recursiveSearch(Node<T> cur, T target) {
        if (cur == null) return null;

        if(target.compareTo(cur.getData()) == 0) return cur.getData();
        else if(target.compareTo(cur.getData()) < 0) return recursiveSearch(cur.getLeft(), target);
        else return recursiveSearch(cur.getRight(), target);
    }

    // -------------------------------------------------------------
    // Recursive Remove Method
    // -------------------------------------------------------------
    public void remove(T data) {root = recursiveRemove(root, data);}
    private Node<T> recursiveRemove(Node<T> cur, T data) {
        if(cur == null) return null;
        if(data.compareTo(cur.getData()) < 0) // go left
            cur.setLeft(recursiveRemove(cur.getLeft(), data));
        else if (data.compareTo(cur.getData()) > 0) // go right
            cur.setRight(recursiveRemove(cur.getRight(), data));
        else { // found node to remove 
            if (cur.getLeft() == null && cur.getRight() == null) return null; // return null if there are no children
            else if(cur.getLeft() == null) return cur.getRight(); 
            else if(cur.getRight() == null) return cur.getLeft();
            else {
                Node<T> leftMax = findMax(cur.getLeft());
                cur.setData(leftMax.getData());
                cur.setLeft(recursiveRemove(cur.getLeft(), leftMax.getData()));
            }
        }
        return cur;
    }

    // -------------------------------------------------------------
    // findMax helper method for Remove method
    // -------------------------------------------------------------
    private Node<T> findMax(Node<T> node) {
        if(node.getRight() == null) return node;
        return findMax(node.getRight());
    }
}
