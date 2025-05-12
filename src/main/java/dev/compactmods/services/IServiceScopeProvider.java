package dev.compactmods.services;

public interface IServiceScopeProvider {

    /**
     * Gets or creates a scope for singleton services. Default scope.
     * @return
     */
    IServiceScope defaultScope();

    /**
     * Gets or creates a keyed service scope.
     * @param scope
     * @return
     */
    <TScopeKey> IServiceScope scope(TScopeKey scope);
}
