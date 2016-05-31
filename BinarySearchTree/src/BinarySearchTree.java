import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

/**
 * 
 * Implementation of most of the Set interface operations using a Binary Search
 * Tree
 *
 * @author Matt Boutell and Brendan Goldacker.
 * @param <T>
 */

public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
	private BinaryNode root;
	// Most of you will prefer to use NULL NODES once you see how to use them.
	final BinaryNode NULL_NODE = new BinaryNode();
	boolean modified = false;

	/**
	 * constructs a binary tree
	 *
	 */
	public BinarySearchTree() {
		this.root = this.NULL_NODE;
	}

	// For manual tests only
	void setRoot(BinaryNode n) {
		this.root = n;
	}

	/**
	 * 
	 * returns the size of the tree
	 *
	 * @return
	 */
	public int size() {
		return this.root.size();
	}

	/**
	 * 
	 * reutrns true if the tree contians the item
	 *
	 * @param item
	 * @return
	 */
	public boolean containsNonBST(T item) {
		return this.root.containsNonBST(item);
	}

	/**
	 * 
	 * returns and array list of the Items in the tree in level order
	 *
	 * @return
	 */
	public ArrayList<T> toArrayList() {
		ArrayList<T> list = new ArrayList<>();
		this.root.fillArrayList(list);
		return list;
	}

	/**
	 * returns and itterator that goes through level
	 * 
	 * @return
	 */
	public Iterator<T> inefficientIterator() {
		return new InefficientIterator();
	}

	/**
	 * reutrns an iterator that goes thorugh preorder
	 *
	 * @return
	 */
	public Iterator<T> preOrderIterator() {
		return new PreOrderIterator(this.root);
	}

	/**
	 * 
	 * checks to see if the tree is empty
	 *
	 * @return
	 */
	public boolean isEmpty() {
		if (this.root == this.NULL_NODE) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * returns the height of the tree
	 *
	 * @return
	 */
	public int height() {
		if (this.root == this.NULL_NODE) {
			return -1;
		}
		return this.root.height();
	}

	/**
	 * returns a string of the elements in the tree
	 *
	 * @return
	 */
	@Override
	public String toString() {
		// this code works btw
		return this.toArrayList().toString();

	}

	/**
	 * returns an array of the tree
	 *
	 * @return
	 */
	public Object[] toArray() {
		System.out.println(this.toArrayList().toString());
		return this.toArrayList().toArray();
	}

	/**
	 * returns an iterator that goes in post order
	 */
	@Override
	public Iterator<T> iterator() {
		return new InOrderIterator(this.root);
	}

	public boolean insert(T item) {
		if(this.root == this.NULL_NODE){
			this.root = new BinaryNode(item);
			this.modified = true;
			return true;
		}
		return this.root.insert(item);
	}
	
	public boolean contains(T item) {
		return this.root.contains(item);
	}
	
	public boolean remove(T item) {
		if(!this.root.contains(item)) {
			return false;
		}
		BinaryNode removed = this.root.remove(item);
		this.root = removed;
		this.modified = true;
		return true;
		
	}

	// ***************************************************************//
	// Not private, since we need access for manual testing.
	@SuppressWarnings("hiding")
	class BinaryNode {
		private T data;
		private BinaryNode left;
		private BinaryNode right;
		private int height;

		/**
		 * Construscts a binary Node, that contains a left, and right node. Each
		 * node contains data
		 * 
		 */
		public BinaryNode() {
			this.data = null;
			this.left = null;
			this.right = null;
		}
		
		public BinaryNode(T element) {
			this.data = element;
			this.left = NULL_NODE;
			this.right = NULL_NODE;
		}

		/**
		 * recursive method that returns the height from the specified node
		 *
		 * @return
		 */
		public int height() {
			// check if no children
			if (this.left == BinarySearchTree.this.NULL_NODE && this.right == BinarySearchTree.this.NULL_NODE) {
				return 0;
				// check only left child
			} else if (this.right == BinarySearchTree.this.NULL_NODE && this.left != BinarySearchTree.this.NULL_NODE) {
				return 1 + this.left.height();
				// check only right child
			} else if (this.left == BinarySearchTree.this.NULL_NODE && this.right != BinarySearchTree.this.NULL_NODE) {
				return 1 + this.right.height();
			}
			// return both children max
			return 1 + Math.max(this.left.height(), this.right.height());
		}

		/**
		 * recursive method that fills an array list with the nodes
		 *
		 * @param list
		 */
		public void fillArrayList(ArrayList<T> list) {
			if (BinaryNode.this == BinarySearchTree.this.NULL_NODE) {
				return;
			}
			this.left.fillArrayList(list);
			list.add(this.data);
			this.right.fillArrayList(list);
		}

		/**
		 * recursive method that returns true if the tree contains the specified
		 * node
		 *
		 * @param item
		 * @return
		 */
		public boolean containsNonBST(T item) {
			if (this == BinarySearchTree.this.NULL_NODE) {
				return false;
			}
			if (this.data.equals(item)) {
				return true;
			}
			return this.left.containsNonBST(item) || this.right.containsNonBST(item);
		}


		public T getData() {
			return this.data;
		}

		public BinaryNode getLeft() {
			return this.left;
		}

		public BinaryNode getRight() {
			return this.right;
		}

		// For manual testing
		public void setLeft(BinaryNode left) {
			this.left = left;
		}

		/**
		 * 
		 * TODO Put here a description of what this method does.
		 *
		 * @param right
		 */
		public void setRight(BinaryNode right) {
			this.right = right;
		}

		/**
		 * a recursive method that returns the size of the tree
		 *
		 * @return
		 */
		public int size() {
			if (this == BinarySearchTree.this.NULL_NODE) {
				return 0;
			}
			return 1 + this.left.size() + this.right.size();

		}

		public boolean insert(T item) throws IllegalArgumentException {
			if (item == null) {
				throw new IllegalArgumentException();
			}
			if (item.compareTo(this.data) < 0) {
				if (this.left == BinarySearchTree.this.NULL_NODE) {
					this.left = (new BinaryNode(item));
					BinarySearchTree.this.modified = true;
					return true;
				}
				return this.left.insert(item);
			}
			if (item.compareTo(this.data) > 0) {
				if (this.right == BinarySearchTree.this.NULL_NODE) {
					this.right = new BinaryNode(item);
					BinarySearchTree.this.modified = true;
					return true;
				}
				return this.right.insert(item);

			}
			return false;
		}
		
		public boolean contains(T item) {
			if(this == NULL_NODE) {
				return false;
			}
			if(item.compareTo(this.data) < 0){
				if(this.left != BinarySearchTree.this.NULL_NODE){
					return this.left.contains(item);
				}
				return false;
			}
			if(item.compareTo(this.data) > 0) {
				if(this.right != BinarySearchTree.this.NULL_NODE){
					return this.right.contains(item);
				}
				return false;
			}
			return true;
		}
		
	
		/**
		 * 
		 * removes an item from the tree, by setting that node equal to 
		 * 
		 */
		public BinaryNode remove(T item) {
			if(item == null) {
				throw new IllegalArgumentException();
			}
			if(this == NULL_NODE) {
				return this;
			}else if(item.compareTo(this.data) < 0){
				this.left = this.left.remove(item);
				return this;
			}else if(item.compareTo(this.data) > 0 ) {
				this.right = this.right.remove(item);
				return this;
			}else {
				if(this.left == NULL_NODE) {
					BinarySearchTree.this.modified = true;
					return this.right;
				}
				if(this.right == NULL_NODE) {
					BinarySearchTree.this.modified = true;
					return this.left;
				}
				//if this has two children
				BinaryNode next = this.left.smallest();
				this.data = next.data;
				this.left = this.left.remove(next.data);
				BinarySearchTree.this.modified = true;
				return this;
			}
		}
		
		/**
		 * returns the smallest value in sub tree 
		 * 
		 */
		private BinaryNode smallest() {
			if(this.right == NULL_NODE) {
				return this;
			}
			return this.right.smallest();
		}

	}

	// ***************************************************************//
	// TODO: Implement your 3 iterator classes here, plus any other inner helper
	// classes you'd like.
	class NodeandVisited extends BinaryNode {
		BinaryNode next;
		boolean hasBeenVisited;

		public NodeandVisited(BinaryNode next, boolean visitied) {
			this.next = next;
			this.hasBeenVisited = visitied;
		}
	}

	/**
	 * an Iterator that iterates level by level
	 *
	 * @author goldacbj. Created Sep 24, 2015.
	 */
	class InefficientIterator implements Iterator<T> {
		private ArrayList<T> list = toArrayList();
		private int index = -1;

		@Override
		public boolean hasNext() {
			return (this.index + 1 < this.list.size());
		}

		@Override
		public T next() throws NoSuchElementException {
			// TODO Auto-generated method stub.
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			this.index++;
			return this.list.get(this.index);
		}

	}

	/**
	 * iterator that iterates level by level
	 *
	 * @author goldacbj. Created Sep 24, 2015.
	 */
	class MysteryIterator {
		private Queue<BinaryNode> queue;

		public MysteryIterator(BinaryNode root) {
			this.queue = new LinkedList<>();

			if (root != BinarySearchTree.this.NULL_NODE) {
				throw new NoSuchElementException();
			}

		}
	}

	/**
	 * Tree iterator that iterates in pre-order
	 *
	 * @author goldacbj. Created Sep 24, 2015.
	 */
	class PreOrderIterator implements Iterator<T> {
		private Stack<T> visited = new Stack<>();
		private T index;
		BinaryNode root;

		public PreOrderIterator(BinaryNode root) {
			BinarySearchTree.this.modified = false;
			this.root = root;
			addPreOrderStack(root);
		}

		@Override
		public boolean hasNext() {
			return !(this.visited.isEmpty());
		}

		@Override
		public T next() throws ConcurrentModificationException {
			if(BinarySearchTree.this.modified == true){
				throw new ConcurrentModificationException();
			}
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			this.index = this.visited.pop();
			return this.index;

		}

		/**
		 * adds nodes the the stack in a way that they can be popped off in
		 * pre-order
		 * 
		 * @param current
		 */
		private void addPreOrderStack(BinaryNode current) {
			if (current == BinarySearchTree.this.NULL_NODE) {
				return;
			}
			// adds the right to the stack
			if (current.getRight() != BinarySearchTree.this.NULL_NODE) {
				addPreOrderStack(current.getRight());
			}
			// adds the left to the stack
			if (current.getLeft() != BinarySearchTree.this.NULL_NODE) {
				addPreOrderStack(current.getLeft());
			}
			// pushs on in opposite to preorder
			this.visited.push(current.getData());

		}
		
		@Override
		public void remove() {
			this.root.remove(this.next());
		}

	}

	class InOrderIterator implements Iterator<T> {
		private Stack<T> postOrderStack = new Stack<>();
		private T indexData;
		private BinaryNode root;

		public InOrderIterator(BinaryNode root) {
			BinarySearchTree.this.modified = false;
			addInOrderStack(root);
			this.root = root;
		}

		@Override
		public boolean hasNext() {
			return !(this.postOrderStack.isEmpty());
		}

		@Override
		public T next() throws ConcurrentModificationException {
			if(BinarySearchTree.this.modified == true){
				throw new ConcurrentModificationException();
			}
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			this.indexData = this.postOrderStack.pop();
			return this.indexData;
		}

		/**
		 * adds items to a stack in a way that they can be popped off the stack
		 * in post order
		 *
		 * @param current
		 */
		private void addInOrderStack(BinaryNode current) {
			if (current == BinarySearchTree.this.NULL_NODE) {
				return;
			}
			if (current.getRight() != BinarySearchTree.this.NULL_NODE) {
				addInOrderStack(current.getRight());
			}
			this.postOrderStack.push(current.getData());
			if (current.getLeft() != BinarySearchTree.this.NULL_NODE) {
				addInOrderStack(current.getLeft());
			}
		}
		
		@Override
		public void remove() {
			this.root.remove(this.next());
		}

	}

}