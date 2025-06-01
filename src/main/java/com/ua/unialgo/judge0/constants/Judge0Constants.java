package com.ua.unialgo.judge0.constants;

/**
 * Constants for Judge0 API integration.
 * Contains status codes and language IDs used by Judge0.
 */
public final class Judge0Constants {

    // Private constructor to prevent instantiation
    private Judge0Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Judge0 submission status codes
     */
    public static final class Status {
        public static final int IN_QUEUE = 1;
        public static final int PROCESSING = 2;
        public static final int ACCEPTED = 3;
        public static final int WRONG_ANSWER = 4;
        public static final int TIME_LIMIT_EXCEEDED = 5;
        public static final int COMPILATION_ERROR = 6;
        public static final int RUNTIME_ERROR_SIGSEGV = 7;
        public static final int RUNTIME_ERROR_SIGXFSZ = 8;
        public static final int RUNTIME_ERROR_SIGFPE = 9;
        public static final int RUNTIME_ERROR_SIGABRT = 10;
        public static final int RUNTIME_ERROR_NZEC = 11;
        public static final int RUNTIME_ERROR_OTHER = 12;
        public static final int INTERNAL_ERROR = 13;
        public static final int EXEC_FORMAT_ERROR = 14;

        private Status() {
        }
    }

    /**
     * Judge0 language IDs for commonly used programming languages
     */
    public static final class Language {
        // C family
        public static final int C_GCC_7_4_0 = 48;
        public static final int C_GCC_8_3_0 = 49;
        public static final int C_GCC_9_2_0 = 50;
        public static final int CPP_GCC_7_4_0 = 52;
        public static final int CPP_GCC_8_3_0 = 53;
        public static final int CPP_GCC_9_2_0 = 54;
        
        // Java
        public static final int JAVA_OPENJDK_13_0_1 = 62;
        
        // Python
        public static final int PYTHON_2_7_17 = 70;
        public static final int PYTHON_3_8_1 = 71;
        
        // JavaScript
        public static final int JAVASCRIPT_NODE_12_14_0 = 63;
        
        // Ruby
        public static final int RUBY_2_7_0 = 72;
        
        // Other popular languages
        public static final int RUST_1_40_0 = 73;
        public static final int TYPESCRIPT_3_7_4 = 74;
        public static final int GO_1_13_5 = 60;
        public static final int KOTLIN_1_3_70 = 78;
        public static final int SCALA_2_13_2 = 81;
        public static final int SWIFT_5_2_3 = 83;
        public static final int PHP_7_4_1 = 68;
        public static final int CSHARP_MONO_6_6_0_161 = 51;
        public static final int HASKELL_8_8_1 = 61;
        public static final int PASCAL_FPC_3_0_4 = 67;
        public static final int PERL_5_28_1 = 85;
        public static final int R_4_0_0 = 80;
        public static final int SQL_SQLITE_3_27_2 = 82;

        private Language() {
        }
    }

    /**
     * Default configuration values
     */
    public static final class Defaults {
        public static final float CPU_TIME_LIMIT = 5.0f;
        public static final int MEMORY_LIMIT = 128000; // in KB (128 MB)
        public static final int MAX_FILE_SIZE = 1024; // in KB (1 MB)
        public static final int MAX_PROCESSES_AND_THREADS = 60;
        public static final boolean BASE64_ENCODED = false;

        private Defaults() {
        }
    }
}