package org.jboss.lectures.auction.ajocado;

import static org.jboss.arquillian.ajocado.Ajocado.elementPresent;
import static org.jboss.arquillian.ajocado.Ajocado.waitModel;
import static org.jboss.arquillian.ajocado.guard.request.RequestTypeGuardFactory.waitHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.jboss.arquillian.api.RunModeType.AS_CLIENT;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.XpathLocator;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
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
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Run(AS_CLIENT)
public class AjocadoTest
{
   @Selenium
   AjaxSelenium driver;

   @ContextPath
   URL contextPath;

   protected XpathLocator EMAIL_INPUT = xp("//input[contains(@id, 'emailInput')]");
   protected XpathLocator LOGIN_BUTTON = xp("//input[contains(@id, 'loginButton')]");
   protected XpathLocator LOGOUT_BUTTON = xp("//input[contains(@id, 'logoutButton')]");
   
   @Deployment
   public static Archive<?> war()
   {

      WebArchive war = ShrinkWrap.create(WebArchive.class, "auction.war")
         .addLibraries(Dependencies.use(MavenDependencies.class)
               .resolveFrom("pom.xml", new ScopeFilter("", "compile")))
         .addPackages(true, NO_TEST_CLASSES, org.jboss.lectures.auction.AuctionManager.class.getPackage());

      war.as(ExplodedImporter.class).importDirectory("src/main/webapp");
      war.addWebResource(new File("src/main/resources/import.sql"), ArchivePaths.create("classes/import.sql"))
      .addManifestResource(new File("src/main/resources/META-INF/persistence.xml"))
      .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));

      war.as(ZipExporter.class).exportTo(new File("auction.war"), true);

      return war;

   }

   @Test
   public void testLogin()
   {
      driver.open(contextPath);
      waitModel.until(elementPresent.locator(EMAIL_INPUT));

      driver.type(EMAIL_INPUT, "kpiwko@redhat.com");

      waitHttp(driver).click(LOGIN_BUTTON);
      
      Assert.assertTrue(driver.isElementPresent(LOGOUT_BUTTON));
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
