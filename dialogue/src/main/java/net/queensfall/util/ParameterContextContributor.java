package net.queensfall.util;

@FunctionalInterface
public interface ParameterContextContributor {
    void contribute(ParameterContext ctx);
}
