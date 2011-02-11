package org.jboss.lectures.auction.simple;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.lectures.auction.validation.City;
import org.jboss.lectures.auction.validation.CityValidator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CityValidatorTest
{
   @Inject
   private CityValidator validator;

   @Deployment
   public static Archive<?> jar()
   {
      return ShrinkWrap.create(JavaArchive.class)
         .addClasses(CityValidator.class, City.class)
         .addManifestResource(EmptyAsset.INSTANCE, "beans.xml");
   }

   @Test
   public void isInBigCity()
   {
      Assert.assertFalse("Ostrava neni dost velke mesto", validator.isValid("Ostrava", null));
   }
}
