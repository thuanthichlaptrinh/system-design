package com.example.ticketing;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/** Cưỡng chế chiều phụ thuộc trong docs/kien-truc.md. */
class ArchitectureTest {

	private static final String ROOT = "com.example.ticketing";

	private final JavaClasses classes = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
			.importPackages(ROOT);

	@Test
	void domainDependsOnNothingOutside() {
		noClasses()
				.that().resideInAPackage(ROOT + ".domain..")
				.should().dependOnClassesThat().resideInAnyPackage(
						"org.springframework..", "jakarta.persistence..",
						ROOT + ".application..", ROOT + ".infrastructure..", ROOT + ".presentation..")
				.allowEmptyShould(true)
				.check(classes);
	}

	@Test
	void applicationDoesNotDependOnOuterLayers() {
		noClasses()
				.that().resideInAPackage(ROOT + ".application..")
				.should().dependOnClassesThat().resideInAnyPackage(
						"jakarta.persistence..", "org.springframework.web..", "org.springframework.data..",
						ROOT + ".infrastructure..", ROOT + ".presentation..")
				.allowEmptyShould(true)
				.check(classes);
	}

	@Test
	void presentationAndInfrastructureDoNotDependOnEachOther() {
		noClasses()
				.that().resideInAPackage(ROOT + ".presentation..")
				.should().dependOnClassesThat().resideInAPackage(ROOT + ".infrastructure..")
				.allowEmptyShould(true)
				.check(classes);
		noClasses()
				.that().resideInAPackage(ROOT + ".infrastructure..")
				.should().dependOnClassesThat().resideInAPackage(ROOT + ".presentation..")
				.allowEmptyShould(true)
				.check(classes);
	}

}
