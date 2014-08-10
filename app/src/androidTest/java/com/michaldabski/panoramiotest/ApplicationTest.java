package com.michaldabski.panoramiotest;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.michaldabski.panoramiotest.models.Photo;
import com.michaldabski.panoramiotest.utils.PhotoDistanceComparator;

import junit.framework.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testComparator() throws Exception
    {
        PhotoDistanceComparator comparator = new PhotoDistanceComparator(0f, 0f);

        assertEquals(0d, PhotoDistanceComparator.distance(0, 0, 0, 0));

        Photo
                nearPhoto = new Photo(0, 0, 1, 1, 0, "", "", "", 0, "Nearby photo", ""),
                mediumPhoto = new Photo(0, 0, 10, 10, 0, "", "", "", 1, "medium photo", ""),
                farPhoto = new Photo(0, 0, 100, 100, 0, "", "", "", 2, "Nearby photo", "");

        List<Photo> photos = Arrays.asList(
                mediumPhoto,
                farPhoto,
                nearPhoto
        );

        Assert.assertEquals(0, comparator.compare(nearPhoto, nearPhoto));
        Assert.assertEquals(0, comparator.compare(mediumPhoto, mediumPhoto));
        Assert.assertEquals(0, comparator.compare(farPhoto, farPhoto));

        Assert.assertEquals(-1, comparator.compare(nearPhoto, farPhoto));
        Assert.assertEquals(-1, comparator.compare(nearPhoto, mediumPhoto));
        Assert.assertEquals(-1, comparator.compare(mediumPhoto, farPhoto));

        Assert.assertEquals(1, comparator.compare(farPhoto, nearPhoto));
        Assert.assertEquals(1, comparator.compare(mediumPhoto, nearPhoto));
        Assert.assertEquals(1, comparator.compare(farPhoto, mediumPhoto));

        Collections.sort(photos, comparator);

        assertEquals(0, photos.indexOf(nearPhoto));
        assertEquals(1, photos.indexOf(mediumPhoto));
        assertEquals(2, photos.indexOf(farPhoto));

        comparator = new PhotoDistanceComparator(53.318938f, -6.403647f);

        nearPhoto = new Photo(0, 0, 53.323321f, -6.395751f, 0, "", "", "", 0, "Nearby photo", "");
        mediumPhoto = new Photo(0, 0, 53.332125f, -6.406866f, 0, "", "", "", 1, "medium photo", "");
        farPhoto = new Photo(0, 0, 53.310861f, -6.490808f, 0, "", "", "", 2, "Nearby photo", "");
        photos = Arrays.asList(
                mediumPhoto,
                farPhoto,
                nearPhoto
        );
        Assert.assertEquals(0, comparator.compare(nearPhoto, nearPhoto));
        Assert.assertEquals(0, comparator.compare(mediumPhoto, mediumPhoto));
        Assert.assertEquals(0, comparator.compare(farPhoto, farPhoto));

        Assert.assertEquals(-1, comparator.compare(nearPhoto, farPhoto));
        Assert.assertEquals(-1, comparator.compare(nearPhoto, mediumPhoto));
        Assert.assertEquals(-1, comparator.compare(mediumPhoto, farPhoto));

        Assert.assertEquals(1, comparator.compare(farPhoto, nearPhoto));
        Assert.assertEquals(1, comparator.compare(mediumPhoto, nearPhoto));
        Assert.assertEquals(1, comparator.compare(farPhoto, mediumPhoto));

        Collections.sort(photos, comparator);

        assertEquals(0, photos.indexOf(nearPhoto));
        assertEquals(1, photos.indexOf(mediumPhoto));
        assertEquals(2, photos.indexOf(farPhoto));
    }
}