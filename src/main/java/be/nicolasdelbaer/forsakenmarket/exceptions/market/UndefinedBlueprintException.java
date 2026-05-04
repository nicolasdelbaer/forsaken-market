package be.nicolasdelbaer.forsakenmarket.exceptions.market;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenException;

public class UndefinedBlueprintException extends ForsakenException {
    public UndefinedBlueprintException(String msg) {
        super(msg);
    }
}
