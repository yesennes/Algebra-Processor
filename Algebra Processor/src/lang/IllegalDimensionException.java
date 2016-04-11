package lang;

import java.io.Serializable;

/**
 * Exception thrown whenever matrix operations are performed on matrices whose
 * dimensions are not correct:
 * In addition or subtraction, by row and column dimensions of both matrices
 * must be the same.
 * In multiplication, the column of the first matrix and the row of the second
 * must match.
 *
 * @author Nikola Istvanic
 */
public class IllegalDimensionException extends RuntimeException
    implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Constructor for unchecked IllegalDimensionException.
     * @param message Error message.
     */
    public IllegalDimensionException(String message) {
        super(message);
    }
}
