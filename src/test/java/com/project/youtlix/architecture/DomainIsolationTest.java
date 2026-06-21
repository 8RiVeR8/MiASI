package com.project.youtlix.architecture;

import com.project.youtlix.architecture.rules.ProductionCodeRules;
import com.project.youtlix.testsupport.annotation.ArchitectureTest;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@ArchitectureTest
class DomainIsolationTest {

    private static final JavaClasses CLASSES = ProductionCodeRules.productionClasses();

    @Test
    void domainDoesNotDependOnSpringOrJpa() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "javax.persistence.."
                );

        rule.check(CLASSES);
    }

    @Test
    void domainDoesNotDependOnInfrastructureOrApplicationLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..application.."
                );

        rule.check(CLASSES);
    }

    @Test
    void applicationDoesNotDependOnInfrastructureAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..");

        rule.check(CLASSES);
    }
}
