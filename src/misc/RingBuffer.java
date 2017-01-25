package misc;


public final class RingBuffer<T>
{
    /* The actual ring buffer. */
    private final T[] elements;

    /* The write pointer, represented as an offset into the array. */
    private int offset = 0;

    /* The read pointer is encoded implicitly by keeping track of the number of
     * unconsumed elements.  We can then determine its position by backing up
     * that many positions before the read position.
     */
    private int unconsumedElements = 0;

    /**
     * Constructs a new RingBuffer with the specified capacity, which must be
     * positive.
     *
     * @param size The capacity of the new ring buffer.
     * @throws IllegalArgumentException If the capacity is negative.
     */
    @SuppressWarnings("unchecked")
    public RingBuffer(int size)
    {
        /* Validate the size. */
        if (size <= 0)
        {
            throw new IllegalArgumentException("RingBuffer capacity must be positive.");
        }

        /* Construct the array to be that size. */
        elements = (T[]) new Object[size];
    }

    /**
     * Appends an element to the ring buffer
     *
     * @param elem The element to add to the ring buffer.
     * insertion completes.
     */
    public synchronized void add(T elem)
    {
        /* Write the element into the next open spot, then advance the write
         * pointer forward a step.
         */
        elements[offset] = elem;
        offset = (offset + 1) % elements.length;

        /* Increase the number of unconsumed elements by one, then notify any
         * threads that are waiting that more data is now available.
         */
        ++unconsumedElements;
    }

    public synchronized void deleteLast()
    {
        if (unconsumedElements == 0)
            return;
        unconsumedElements--;

        offset--;
        if (offset == -1)
            offset = elements.length-1;
    }

    /**
     * Returns the maximum capacity of the ring buffer.
     *
     * @return The maximum capacity of the ring buffer.
     */
    public int capacity()
    {
        return elements.length;
    }

    /**
     * Observes, but does not dequeue, the next available element, blocking
     * until data becomes available.
     *
     * @return The next available element.
     * becomes available.
     */
    public synchronized T peek()
    {
        try
        {
            return elements[(offset + (capacity() - unconsumedElements)) % capacity()];
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Removes and returns the next available element, 
     *
     * @return The next available element
     * becomes available.
     */
    public synchronized T remove()
    {
        if (unconsumedElements == 0)
            return null;

        /* Use peek() to get the element to return. */
        T result = peek();

        /* Mark that one fewer elements are now available to read. */
        --unconsumedElements;

        return result;
    }

    public synchronized void clear()
    {
        while (remove()!=null)
        {
            // do nothing
        }
    }

    /**
     * Returns the number of elements that are currently being stored in the
     * ring buffer.
     *
     * @return The number of elements currently stored in the ring buffer.
     */
    public synchronized int size()
    {
        return unconsumedElements;
    }

    /**
     * Returns whether the ring buffer is empty.
     *
     * @return Whether the ring buffer is empty.
     */
    public synchronized boolean isEmpty()
    {
        return size() == 0;
    }
}
