package net.frankheijden.wecompatibility.core;

public class SetResult {

    private boolean result;
    private Super superCall;

    public SetResult(boolean result, Super superCall) {
        this.result = result;
        this.superCall = superCall;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Super getSuperCall() {
        return superCall;
    }

    public void setSuperCall(Super superCall) {
        this.superCall = superCall;
    }
}
