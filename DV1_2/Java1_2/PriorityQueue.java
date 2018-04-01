import java.util.*;

// A priority queue.
public class PriorityQueue<E> {
    private ArrayList<E> heap = new ArrayList<E>();
    private Comparator<E> comparator;

    //Mark Step [3.1] - Map Declaration

    private Map<E,Integer > hmap = new HashMap<>();

    //Mark Step [ ^ ]

    public PriorityQueue(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    // Returns the size of the priority queue.
    public int size() {
        return heap.size();
    }

    /*

     Theory: Hmap will hold <E.hashcode, E.heapIndex>
             -> In update, check if E.hashcode Exists if yes             -Check
                -> Get the E.heapIndex bound to E.hashcode               -Check
                -> Put the replacement heap with E.heapIndex             -Check
                -> Delete the old entry of <E, E> replace with same      -Check
                   Index & Replacement.hashCode()                        -Check
             -> In add, set a new entry to Hmap with the index and x.    -Check
             -> SiftUp/Down update the Hmap entries with new values.


     */



    public void update(E original, E replacement){
        //MARK Step [3.2] - new Update

        Integer valueToken;
        if(hmap.containsKey(original) == true){
            valueToken = hmap.get(original);
            heap.set(hmap.get(original), replacement);
            hmap.remove(original);
            hmap.put(replacement,valueToken );

            if( comparator.compare(original,replacement) > 0) {
                siftDown(valueToken);

            }
            else {
                siftUp(valueToken);

            }
        }
        //MARK Step [ ^ ]
/*
        //hmap.containsValue(original) == true
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).hashCode() == original.hashCode()){
                heap.set(i, replacement);

                if( comparator.compare(original,replacement) > 0) {
                    siftDown(i);
                    break;
                }
                else {
                    siftUp(i);
                    break;
                }

            }

        }*/
    }

    // Adds an item to the priority queue.
    public void add(E x) {
       // assert invariant(): showHeap();

        heap.add(heap.size(), x);

        //Mark Step [3.3] - Add to Map
        hmap.put(x,heap.size()-1);

        //Mark Step [ ^ ]

        siftUp(heap.size()-1);


      //  assert invariant(): showHeap();
    }

    // Returns the root in the priority queue.
    // Throws NoSuchElementException if empty.
    public E minimum() {
        if (size() == 0){
            throw new NoSuchElementException();
        }

        return heap.get(0);
    }

    // Removes the smallest item in the priority queue.
    // Throws NoSuchElementException if empty.
    public void deleteMinimum() {
        if (size() == 0)
            throw new NoSuchElementException();

        heap.set(0, heap.get(heap.size()-1));

        //Mark Step [3.4] - Remove minimum from Hash.

        hmap.remove(minimum());

        //Mark Step [ ^ ]

        heap.remove(heap.size()-1);
        if (heap.size() > 0){
            siftDown(0);
        }



    }

    // Sifts a node up.
    // siftUp(index) fixes the invariant if the element at 'index' may
    // be less than its parent, but all other elements are correct.
    private void siftUp(int index) {
       //MARK Not part of OG code
         E value = heap.get(index);

        //Stops when root is reached
        while (index > 0) {
            int parent= parent(index);
            E parentValue= heap.get(parent);//parent index

            if (comparator.compare(value, parentValue) > 0) {

                hmap.remove(parentValue);

                heap.set(index, parentValue);

                hmap.put(parentValue,index);

                index = parent;
            } else {break;}
        }
        hmap.remove(value);
        heap.set(index, value);
        hmap.put(value, index);
       //MARK end of part
    }

    // Sifts a node down.
    // siftDown(index) fixes the invariant if the element at 'index' may
    // be greater than its children, but all other elements are correct.
    private void siftDown(int index) {
        E value = heap.get(index);

        // Stop when the node is a leaf.
        while (leftChild(index) < heap.size()) {
            int left    = leftChild(index);
            int right   = rightChild(index);

            // Work out whether the left or right child is smaller.
            // Start out by assuming the left child is smaller...
            int child = left;
            E childValue = heap.get(left);

            // ...but then check in case the right child is smaller.
            // (We do it like this because maybe there's no right child.)
            if (right < heap.size()) {
                E rightValue = heap.get(right);
                if (comparator.compare(rightValue, childValue ) > 0) {
                    child = right;
                    childValue = rightValue;
                }
            }

            // If the child is smaller than the parent,
            // carry on downwards.
            if (comparator.compare(childValue, value) > 0) {

                hmap.remove(childValue);
                heap.set(index, childValue);
                hmap.put(childValue,index);

                index = child;
            } else {break;}
        }

        hmap.remove(value);
        heap.set(index, value);
        hmap.put(value,index);

    }

    // Helper functions for calculating the children and parent of an index.
    private final int leftChild(int index) {
        return 2*index+1;
    }

    private final int rightChild(int index) {
        return 2*index+2;
    }

    private final int parent(int index) {
        return (index-1)/2;
    }


    private boolean invariant(){
        // TODO: return true if and only if the heap invariant is true.
        //MARK IS broken.

        int i=0;
        if (heap.size() == 0){
            return true;
        }
        while(i>=(heap.size()-1)/2){// (heap.size()-1)/2 is the fromula for calculating the floor of the tree (n-1)/2
            if (comparator.compare(heap.get(i), heap.get(leftChild(i))) <0 || comparator.compare(heap.get(i), heap.get(rightChild(i))) <0) {
                return false;
            }
            i++;
        }
        for(int j = 0; j< heap.size();j++){
            if( hmap.containsValue(j) == false){
                return false;
            }
        }
        return true;

    }

    private String showHeap(){
        // TODO: return description of heap contents.
        int i=0;
        while (i<heap.size()){
            System.err.print(heap.get(i).toString());
        }

        throw new UnsupportedOperationException();
    }
}