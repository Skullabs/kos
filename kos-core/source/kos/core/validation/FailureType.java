package kos.core.validation;

public enum FailureType {

    /**
     * Indicates that the server understands the content of this entity
     * and, apparently, all statically required attributes is present.
     * However, but it was unable to process the contained instructions.
     */
    UNPROCESSABLE_ENTITY,

    /**
     * Indicates that the server cannot or will not process the entity
     * due to something that is perceived to be a client error (e.g.,
     * malformed request syntax, invalid request message framing, or
     * deceptive request routing).
     */
    MALFORMED_ENTITY,

    /**
     * Other failure reason
     */
    OTHER
}