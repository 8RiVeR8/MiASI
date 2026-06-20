package com.project.youtlix.architecture.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reusable ArchUnit rules for production code. Extend this class as the rule set grows.
 */
public final class ProductionCodeRules {

    private static final String ROOT_PACKAGE = "com.project.youtlix";

    private ProductionCodeRules() {
    }

    public static JavaClasses productionClasses() {
        return new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT_PACKAGE);
    }

    public static ArchRule productionCodeMustNotDependOnJUnit() {
        return noClasses()
                .that().resideInAPackage(ROOT_PACKAGE + "..")
                .should().dependOnClassesThat().resideInAnyPackage("org.junit..");
    }
}
