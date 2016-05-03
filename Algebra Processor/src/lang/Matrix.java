package lang;

import java.io.Serializable;

/**
 * Class representation of an algebraic matrix which is R x C. Matrix operations
 * add, subtract, multiply, invert, and compute determinant are valid.
 * Matrices can also be vectors (where C = 1).
 *
 * @author Nikola Istvanic, Mason Liu
 * @version 1.0
 */
public class Matrix implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Two-dimensional array representation of this matrix.
     */
    private Constant[][] matrix;
    /**
     * Number of rows in this matrix.
     */
    public int row;
    /**
     * Number of columns in this matrix.
     */
    public int col;

    /**
     * Zero matrix constructor which takes the number of matrix rows and
     * columns.
     * @throws IllegalArgumentException Whenever a nonpositive value is entered
     * for row or col.
     * @param row The number of rows in this matrix.
     * @param col The number of columns in this matrix.
     */
    public Matrix(int row, int col) {
        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException(row <= 0
                    ? "Must have positive number of rows."
                            : "Must have positive number of columns.");
        }
        this.row = row;
        this.col = col;
        matrix = new Constant[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                matrix[i][j] = new Constant();
            }
        }
    }

    /**
     * Nonzero matrix constructor which is created from the two-dimensional
     * array parameter.
     * @param matrix The two-dimensional array on which this matrix is based.
     */
    public Matrix(Constant[][] matrix) {
        row = matrix.length;
        col = matrix[0].length;
        this.matrix = matrix;
    }

    /**
     * Addition method which takes two matrices of equal dimensions and adds
     * their corresponding entries to a new matrix which is returned.
     * @throws IllegalDimensionException If the dimensions of the parameter
     * matrix do not match those of the current matrix.
     * @param addend The other matrix being added.
     * @return A matrix whose entries are the sums of the respective entries of
     * this matrix and the parameter matrix.
     */
    public Matrix add(Matrix addend) throws IllegalDimensionException {
        if (addend.row != row || addend.col != col) {
            throw new IllegalDimensionException("Cannot add due to nonmatching "
                    + (addend.row != row ? "row" : "column") + " dimensions.");
        }
        Constant[][] sum = new Constant[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                sum[i][j] = matrix[i][j].add(addend.matrix[i][j]);
            }
        }
        return new Matrix(sum);
    }

    /**
     * Subtraction method which takes two matrices of equal dimensions and
     * computes the difference of their corresponding entries to a new matrix
     * which is returned.
     * @throws IllegalDimensionException If the dimensions of the parameter
     * matrix do not match those of the current matrix.
     * @param subtrahend The other matrix being subtracted.
     * @return A matrix whose entries are the differences of the respective
     * entries of this matrix and the parameter matrix.
     */
    public Matrix subtract(Matrix subtrahend) throws IllegalDimensionException {
        if (subtrahend.row != row || subtrahend.col != col) {
            throw new IllegalDimensionException("Cannot subtract due to "
                    + "nonmatching " + (subtrahend.row != row
                        ? "row" : "column") + " dimensions.");
        }
        Constant[][] difference = new Constant[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                difference[i][j]
                        = matrix[i][j].subtract(subtrahend.matrix[i][j]);
            }
        }
        return new Matrix(difference);
    }

    /**
     * Method for multiplying matrices where the column dimension of this matrix
     * matches the row dimension of the parameter matrix.
     * @throws IllegalDimensionException If the row of the parameter matrix does
     * not match the column dimension of the current matrix.
     * @param multiplicand The matrix being multiplied by this matrix.
     * @return The product of these two matrices.
     */
    public Matrix multiply(Matrix multiplicand)
            throws IllegalDimensionException {
        if (multiplicand.row != col) {
            throw new IllegalDimensionException("The row dimension does not "
                    + "match the column dimension.");
        }
        Matrix product = new Matrix(row, multiplicand.col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < multiplicand.col; j++) {
                for (int k = 0; k < col; k++) {
                    product.matrix[i][j] = product.matrix[i][j]
                            .add(matrix[i][k]
                                    .multiply(multiplicand.matrix[k][j]));
                }
            }
        }
        return product;
    }

    /**
     * Method that finds the determinant of the current matrix.
     * @throws IllegalDimensionException If the matrix is not square.
     * @return The Constant value of the determinant of this matrix.
     */
    public Constant determinant() throws IllegalDimensionException {
        if (row != col) {
            throw new IllegalDimensionException("Can only compute determinant "
                    + "of square matrix.");
        }
        Matrix[] lu = LUFactorization();
        int count = 0;
        for (int i = 0; i < lu[0].row; i++) {
            if (!lu[0].matrix[i][i].equals(Constant.ONE)) {
                count++;
            }
        }
        if (count == 0) {
            count++;
        }
        Constant determinant = new Constant((int) Math.pow(-1, count - 1));
        for (int i = 0; i < lu[2].row; i++) {
            determinant = determinant.multiply(lu[2].matrix[i][i]);
        }
        return determinant;
    }

    /**
     * Returns the inverse of the matrix.
     * @throws IllegalDimensionException If a non-square matrix is used.
     * @return The inverse of this matrix.
     */
    public Matrix inverse() throws IllegalDimensionException {
        if (row != col) {
            throw new IllegalDimensionException("Can only compute inverse "
                    + "of square matrix.");
        }
        Constant[][] matrix = new Constant[row][col],
                inverse = identity(row).matrix;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = this.matrix[i][j].clone();
            }
        }
        if (matrix.length == 0 || matrix[0].length == 0) {
            return null;
        }
        Constant ZERO = new Constant();
        int[] pivotCols = new int[matrix.length];
        int pivotRow = 0;
        for (int i = 0; i < matrix[0].length && pivotRow < matrix.length; i++) {
            if (matrix[pivotRow][i].equals(ZERO)) {
                boolean isDone = false;
                for(int j = pivotRow + 1; j < matrix.length && !isDone; j++) {
                    if(!matrix[j][i].equals(ZERO)) {
                        interChange(matrix, pivotRow, j);
                        interChange(inverse, pivotRow, j);
                        isDone = true;
                    }
                }
            }
            pivotCols[pivotRow] = -1;
            if (!matrix[pivotRow][i].equals(ZERO)) {
                pivotCols[pivotRow] = i;
                for (int j = pivotRow + 1; j < matrix.length; j++) {
                    if (!matrix[j][i].equals(ZERO)) {
                        Constant pRowScalar = matrix[j][i];
                        scaleRow(matrix, matrix[pivotRow][i], j);
                        scaleRow(inverse, matrix[pivotRow][i], j);
                        for (int k = 0; k < matrix[j].length; k++) {
                            matrix[j][k] = matrix[j][k].subtract(
                                    matrix[pivotRow][k].multiply(pRowScalar));
                            inverse[j][k] = inverse[j][k].subtract(
                                    inverse[pivotRow][k].multiply(pRowScalar));
                        }
                    }
                }
                pivotRow++;
            }
        }
        for (int i = matrix.length - 1; i > 0; i--) {
            if (pivotCols[i] != -1) {
                for (int j = i - 1; j >= 0; j--) {
                    Constant pRowScalar = matrix[j][i];
                    scaleRow(matrix, matrix[i][pivotCols[i]], j);
                    scaleRow(inverse, inverse[i][pivotCols[i]], j);
                    for (int k = 0; k < matrix[j].length; k++) {
                        matrix[j][k] = matrix[j][k].subtract(
                                matrix[i][k].multiply(pRowScalar));
                        inverse[j][k] = inverse[j][k].subtract(
                                inverse[i][k].multiply(pRowScalar));
                    }
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            boolean scale = false;
            Constant divisor = new Constant();
            for (int j = 0; j < matrix[i].length; j++) {
                if (!matrix[i][j].equals(ZERO) && !scale) {
                    scale = true;
                    divisor = matrix[i][j];
                }
                if (scale) {
                    matrix[i][j] = matrix[i][j].divide(divisor);
                    inverse[i][j] = inverse[i][j].divide(divisor);
                }
            }
        }
        return new Matrix(matrix).equals(Matrix.identity(row))
                ? new Matrix(inverse) : null;
    }

    /**
     * Gauss-Jordan Elimination method which solves the current matrix.
     * @return The solved matrix.
     */
    public Matrix gaussJordan() {
        Constant[][] matrix = new Constant[row][col];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = this.matrix[i][j].clone();
            }
        }
        if (matrix.length == 0 || matrix[0].length == 0) {
            return null;
        }
        Constant ZERO = new Constant();
        int[] pivotCols = new int[matrix.length];
        int pivotRow = 0;
        for (int i = 0; i < matrix[0].length && pivotRow < matrix.length; i++) {
            if (matrix[pivotRow][i].equals(ZERO)) {
                boolean isDone = false;
                for (int j = pivotRow + 1; j < matrix.length && !isDone; j++) {
                    if (!matrix[j][i].equals(ZERO)) {
                        interChange(matrix, pivotRow, j);
                        isDone = true;
                    }
                }
            }
            pivotCols[pivotRow] = -1;
            if (!matrix[pivotRow][i].equals(ZERO)) {
                pivotCols[pivotRow] = i;
                for (int j = pivotRow + 1; j < matrix.length; j++) {
                    if (!matrix[j][i].equals(ZERO)) {
                        Constant pRowScalar = matrix[j][i];
                        scaleRow(matrix, matrix[pivotRow][i], j);
                        for (int k = 0; k < matrix[j].length; k++) {
                            matrix[j][k] = matrix[j][k].subtract(
                                    matrix[pivotRow][k].multiply(pRowScalar));
                        }
                    }
                }
                pivotRow++;
            }
        }
        for (int i = matrix.length - 1; i > 0; i--) {
            if (pivotCols[i] != -1) {
                for (int j = i - 1; j >= 0; j--) {
                    Constant pRowScalar = matrix[j][i];
                    scaleRow(matrix, matrix[i][pivotCols[i]], j);
                    for (int k = 0; k < matrix[j].length; k++) {
                        matrix[j][k] = matrix[j][k].subtract(
                                matrix[i][k].multiply(pRowScalar));
                    }
                }
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            boolean scale = false;
            Constant divisor = new Constant();
            for (int j = 0; j < matrix[i].length; j++) {
                if (!matrix[i][j].equals(ZERO) && !scale) {
                    scale = true;
                    divisor = matrix[i][j];
                }
                if (scale) {
                    matrix[i][j] = matrix[i][j].divide(divisor);
                }
            }
        }
        return new Matrix(matrix);
    }

    /**
     * Interchanges two rows of a matrix.
     * @throws IllegalArgumentException If the matrix parameter is null or the
     * rows entered are greater than the number the matrix has or are negative.
     * @param matrix The matrix to perform operations on.
     * @param r1 The first row.
     * @param r2 The second row.
     */
    private void interChange(Constant[][] matrix, int r1, int r2) {
        if (matrix == null || r1 > matrix.length || r2 > matrix.length
                || r1 < 0 || r2 < 0) {
            throw new IllegalArgumentException(
                    "Bad row interchange parameters.");
        }
        for (int i = 0; i < matrix[r1].length; i++) {
            Constant temp = matrix[r1][i];
            matrix[r1][i] = matrix[r2][i];
            matrix[r2][i] = temp;
        }
    }

    /**
     * Scales one row of a matrix.
     * @throws IllegalArgumentException If the matrix entered is null or if the
     * row parameter is below zero or above the number of columns in the matrix.
     * @param matrix The matrix to perform operations on.
     * @param scalar The constant to scale by.
     * @param row The row to scale.
     */
    private void scaleRow(Constant[][] matrix, Constant scalar, int row) {
        if (matrix == null || row > matrix.length || row < 0) {
            throw new IllegalArgumentException("Bad row scale parameters.");
        }
        for (int i = 0; i < matrix[row].length; i++) {
            matrix[row][i] = matrix[row][i].multiply(scalar);
        }
    }

    /**
     * Returns the LU Factorization where P is the permutation matrix, L is
     * lower triangular, and U is upper triangular.
     * @return The array of Matrices P, L, U where PA = LU
     */
    public Matrix[] LUFactorization() {
        Constant[][] l = Matrix.identity(row).matrix,
                u = new Constant[row][col], p = Matrix.identity(row).matrix;
        for (int i = 0; i < u.length; i++) {
            for (int j = 0; j < u[i].length; j++) {
                u[i][j] = this.matrix[i][j].clone();
            }
        }
        if (u.length == 0 || u[0].length == 0) {
            return null;
        }
        Constant ZERO = new Constant();
        int pivotRow = 0;
        for (int i = 0; i < u[0].length && pivotRow < u.length; i++) {
            if (u[pivotRow][i].equals(ZERO)) {
                boolean isDone = false;
                for (int j = pivotRow + 1; j < u.length && !isDone; j++) {
                    if (!u[j][i].equals(ZERO)) {
                        interChange(u, pivotRow, j);
                        interChange(p, pivotRow, j);
                        isDone = true;
                    }
                }
            }
            if (!u[pivotRow][i].equals(ZERO)) {
                for (int j = pivotRow + 1; j < u.length; j++) {
                    if (!u[j][i].equals(ZERO)) {
                        Constant pRowScalar = u[j][i];
                        scaleRow(u, u[pivotRow][i], j);
                        l[j][i] = pRowScalar.divide(u[pivotRow][i]);
                        for (int k = 0; k < u[j].length; k++) {
                            u[j][k] = u[j][k].subtract(u[pivotRow][k].multiply(
                                    pRowScalar)).divide(u[pivotRow][i]);
                        }
                    }
                }
                pivotRow++;
            }
        }
        return new Matrix[]{new Matrix(p), new Matrix(l), new Matrix(u)};
    }

    /**
     * Generates an identity matrix of size dimension x dimension.
     * @param dimension The number of rows or columns in the identity matrix.
     * @return The identity matrix.
     */
    public static Matrix identity(int dimension) {
        Constant[][] identity = new Constant[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                identity[i][j] = new Constant(i == j ? 1 : 0);
            }
        }
        return new Matrix(identity);
    }

    /**
     * Hashcode method which returns the hash of this matrix.
     * @return This matrix's hash code.
     */
    @Override
    public int hashCode() {
        Constant hashCode = new Constant(0), base = new Constant(17);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                hashCode = hashCode.add(matrix[i][j].multiply(base));
            }
        }
        return hashCode.intValue();
    }

    /**
     * Equals method that determines if two matrices are equal, based on the
     * values in each matrix.
     * @param object The other matrix.
     * @return True if the matrices are equal; false if they are not.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Matrix)) {
            return false;
        }
        if (object == this) {
            return true;
        }
        Matrix other = (Matrix) object;
        if (other.row != row || other.col != col) {
            return false;
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (!matrix[i][j].equals(other.matrix[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        int[] maxLength = new int[col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                maxLength[j] = Math.max(maxLength[j],
                        matrix[i][j].toString().length());
            }
        }
        StringBuilder toString = new StringBuilder();
        for (int i = 0; i < row; i++) {
            toString.append("| ");
            for (int j = 0; j < col; j++) {
                int length = matrix[i][j].toString().length();
                for (int k = 0; k < maxLength[j] - length; k++) {
                    toString.append(" ");
                }
                toString.append(matrix[i][j].toString() + " ");
            }
            toString.append("| \n");
        }
        toString.setLength(toString.length() - 2);
        return toString.toString();
    }
}
