package cc.minetale.pigeon;

import com.google.gson.JsonElement;

import java.lang.reflect.Type;

public abstract class Converter<T> {

    protected Class<? extends T> type;

    protected boolean standardCheck;
    protected boolean allowForSubtypes;

    protected Converter(Class<? extends T> type, boolean standardCheck, boolean allowForSubtypes) {
        this.type = type;
        this.standardCheck = standardCheck;
        this.allowForSubtypes = allowForSubtypes;
    }

    /**
     * Override to allow for custom checking if the {@link Converter} is appropriate for the given {@link Type}.
     * <strong>standardCheck</strong> needs to be set to <strong>false</strong> for this to be called.
     * @param type {@link Type} to use when checking if this {@link Converter} is applicable.
     * @return Whether this {@link Converter} should be used.
     */
    public boolean customCheck(Type type) { throw new UnsupportedOperationException(); }

    public abstract T convertToValue(Type type, JsonElement element);
    public abstract JsonElement convertToSimple(Type type, T value);

}
