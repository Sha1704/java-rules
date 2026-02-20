public class OBJ56 {

    // Sensitive mutable class
    static class SensitiveClass {
        private String secret;

        public SensitiveClass(String secret) {
            this.secret = secret;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    // Read-only wrapper that extends the sensitive class
    static class ReadOnlyClass extends SensitiveClass {

        public ReadOnlyClass(String secret) {
            super(secret);
        }

        @Override
        public String getSecret() {
            // Return a safe copy (String is immutable, so just return it)
            return super.getSecret();
        }

        @Override
        public void setSecret(String secret) {
            throw new UnsupportedOperationException("Modification not allowed");
        }
    }

    public static void main(String[] args) {

        // Safe to expose as SensitiveClass because wrapper blocks mutation
        SensitiveClass safeView = new ReadOnlyClass("Top Secret");
        System.out.println("Safe view created.");
        System.out.println("Secret: " + safeView.getSecret());
        System.out.println("Attempting to modify secret...");
        try {
            safeView.setSecret("New Secret");
        } catch (UnsupportedOperationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}