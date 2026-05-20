package net.queensfall.parameter;

@FunctionalInterface
public interface ParameterContextContributor {
    void contribute(ParameterContext ctx);
}
