package com.ai.dataCleansing.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private boolean valid;
    private List<String> errors;

    // 构造函数
    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    // Getter
    public boolean isValid() { return valid; }
    public List<String> getErrors() { return errors; }
}
