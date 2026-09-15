package com.bhagavan.lastwish.model;

public final class Enums {
    private Enums() {}

    public enum WishStatus { ACTIVE, ARCHIVED }
    public enum AccessLevel { VIEW, DOWNLOAD }
    public enum VerificationStatus { PENDING, APPROVED, REJECTED }
    public enum DocumentType { IDENTITY, SUPPORTING, OTHER }
    public enum Role { USER, ADMIN }
}
