package com.palm3.createutils.foundation.lang;

import com.simibubi.create.foundation.data.CreateRegistrate;

import javax.annotation.Nullable;
import java.util.HashMap;

public class LangRegistrator {

    private final HashMap<String, String> lang = new HashMap<>();
    private final CreateRegistrate registrate;

    public LangRegistrator(CreateRegistrate registrate) {
        this.registrate = registrate;
    }

    public void add(String key, String value) {
        lang.put(key, value);
    }

    @Deprecated
    /// @deprecated this method should not be directly used to register lang, use {@link #addTooltip(String, Tooltips, String)} instead.
    public void addTooltipNum(String baseKey, Tooltips type, @Nullable Integer index, String value) {
        if (index == null || index <= 0)
            throw new IllegalArgumentException("Create tooltips lang entries start from 1, '" + index + "' is not accepted!");
        add(baseKey + type.keyPiece + (type == Tooltips.SUMMARY ? "" : index.toString()), value);
    }

    public void addTooltip(String baseKey, Tooltips type, String value) {
        addTooltipNum(baseKey, type, null, value);
    }

    public PartialTooltipLang addTooltips(String baseKey, Tooltips type, String value) {
        addTooltipNum(baseKey, type, null, value);
        return new PartialTooltipLang();
    }

    public void registerAll() {
        lang.forEach(registrate::addRawLang);
    }

    public static class PartialTooltipLang {

    }
}
