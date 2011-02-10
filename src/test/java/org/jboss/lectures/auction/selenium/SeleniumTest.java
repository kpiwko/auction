package org.jboss.lectures.auction.selenium;

import java.io.File;
import java.net.URI;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.selenium.annotation.ContextPath;
import org.jboss.arquillian.selenium.annotation.Selenium;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.Dependencies;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.jboss.shrinkwrap.dependencies.impl.filter.ScopeFilter;
import org.jboss.shrinkwrap.impl.base.importer.ExplodedImporterImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.thoughtworks.selenium.DefaultSelenium;

@RunWith(Arquillian.class)
@Run(RunModeType.AS_CLIENT)
public class SeleniumTest
{
   @Selenium
   DefaultSelenium selenium;
   
   @ContextPath URI contextPath;

   @Deployment
   public static Archive<?> war()
   {

      WebArchive war = ShrinkWrap.create(WebArchive.class, "auction.war")
            .addLibraries(Dependencies.use(MavenDependencies.class).resolveFrom("pom.xml", new ScopeFilter("", "compile")))
            .addPackages(true, NO_TEST_CLASSES, org.jboss.lectures.auction.AuctionManager.class.getPackage());
      
         war.as(ExplodedImporter.class).importDirectory("src/main/webapp");
         war.addWebResource(new File("src/main/resources/import.sql"), ArchivePaths.create("classes/import.sql"))
            .addManifestResource(new File("src/main/resources/META-INF/persistence.xml"))
            .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
     
      war.as(ZipExporter.class).exportTo(new File("/tmp/test.war"), true);

      return war;

   }

   @Test
   public void testLogin()
   {
      selenium.open(contextPath + "/index.html");

      selenium.type("tester@tester.org", "xpath=//input[contains(@id, 'emailInput')]");
      selenium.click("xpath=//input[contains(@id, 'loginButton')]");
   }

   private static final Filter<ArchivePath> NO_TEST_CLASSES = new Filter<ArchivePath>()
   {

      @Override
      public boolean include(ArchivePath object)
      {
         return !object.get().contains("Test");
      }

   };

}
