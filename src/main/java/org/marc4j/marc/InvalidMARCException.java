
package org.marc4j.marc;

/**
 * A runtime exception thrown when invalid MARC is constructed.
 * 
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public class InvalidMARCException extends RuntimeException {
    /**
     * A <code>serialVersionUID</code> for the class.
     */
    private static final long serialVersionUID = -3289277670563289403L;

    /**
     * An exception thrown when invalid MARC is encountered.
     */
    public InvalidMARCException() {
        super();
    }

    /**
     * An exception thrown when invalid MARC is encountered; included in the
     * exception is a detailed exception message.
     * 
     * @param aMessage More information about the exception
     */
    public InvalidMARCException(String aMessage) {
        super(aMessage);
    }

    /**
     * An exception thrown when invalid MARC is constructed; included in the
     * exception is a parent exception.
     * 
     * @param aCause More information about the exception
     */
    public InvalidMARCException(Exception aCause) {
        super(aCause);
    }

    /**
     * An exception thrown when invalid MARC is constructed; included in the
     * exception is a detailed exception message and a parent exception.
     * 
     * @param aMessage More information about the exception
     * @param aCause A parent exception
     */
    public InvalidMARCException(String aMessage, Exception aCause) {
        super(aMessage, aCause);
    }

}
