package org.esa.snap.multiply.tools;

/**
 * Constants for SNAP MULTIPLY Tools.
 *
 * @author olafd
 */
public class SnapMultiplyConstants {

    public static final int MODIS_TILE_WIDTH = 1200;
    public static final int MODIS_TILE_HEIGHT = 1200;

    public static final double MODIS_SIN_PROJECTION_PIXEL_SIZE_X = 926.6254330558;
    public static final double MODIS_SIN_PROJECTION_PIXEL_SIZE_Y = 926.6254330558;

    public static final String MODIS_SIN_PROJECTION_CRS_STRING =
            "PROJCS[\"MODIS Sinusoidal\"," +
                    "GEOGCS[\"WGS 84\"," +
                    "  DATUM[\"WGS_1984\"," +
                    "    SPHEROID[\"WGS 84\",6378137,298.257223563," +
                    "      AUTHORITY[\"EPSG\",\"7030\"]]," +
                    "    AUTHORITY[\"EPSG\",\"6326\"]]," +
                    "  PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]]," +
                    "  UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]]," +
                    "   AUTHORITY[\"EPSG\",\"4326\"]]," +
                    "PROJECTION[\"Sinusoidal\"]," +
                    "PARAMETER[\"false_easting\",0.0]," +
                    "PARAMETER[\"false_northing\",0.0]," +
                    "PARAMETER[\"central_meridian\",0.0]," +
                    "PARAMETER[\"semi_major\",6371007.181]," +
                    "PARAMETER[\"semi_minor\",6371007.181]," +
                    "UNIT[\"m\",1.0]," +
                    "AUTHORITY[\"SR-ORG\",\"6974\"]]";

}
