package net.lordimass.dialogue.parameter;

@FunctionalInterface
public interface ParameterContextContributor {
    void contribute(ParameterContext ctx);
}
