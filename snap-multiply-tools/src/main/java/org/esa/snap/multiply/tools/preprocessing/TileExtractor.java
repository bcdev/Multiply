package org.esa.snap.multiply.tools.preprocessing;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.common.reproject.ReprojectionOp;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.multiply.tools.SnapMultiplyConstants;

import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Extracts geometry of MODIS tile and reprojects.
 *
 */
class TileExtractor {

    static Product reprojectToModisTile(Product sourceProduct,
                                                String tileName,
                                                double scaleFactor) {
        ModisTileCoordinates modisTileCoordinates = ModisTileCoordinates.getInstance();
        int tileIndex = modisTileCoordinates.findTileIndex(tileName);
        if (tileIndex == -1) {
            throw new OperatorException("Found no tileIndex for tileName=''" + tileName + "");
        }
        double easting = modisTileCoordinates.getUpperLeftX(tileIndex);
        double northing = modisTileCoordinates.getUpperLeftY(tileIndex);

        ReprojectionOp repro = new ReprojectionOp();
        repro.setParameterDefaultValues();
        repro.setParameter("easting", easting);
        repro.setParameter("northing", northing);
        repro.setParameter("crs", SnapMultiplyConstants.MODIS_SIN_PROJECTION_CRS_STRING);
        // repro.setParameter("resampling", "Nearest");  // we may get artefacts at tile boundaries with this one
        repro.setParameter("resampling", "Bilinear");
        repro.setParameter("includeTiePointGrids", true);
        repro.setParameter("referencePixelX", 0.0);
        repro.setParameter("referencePixelY", 0.0);
        repro.setParameter("orientation", 0.0);

        // scale factor > 1 increases pixel size and decreases number of pixels;
        final double pixelSizeX = SnapMultiplyConstants.MODIS_SIN_PROJECTION_PIXEL_SIZE_X * scaleFactor;
        final double pixelSizeY = SnapMultiplyConstants.MODIS_SIN_PROJECTION_PIXEL_SIZE_Y * scaleFactor;
        final int width = (int) (SnapMultiplyConstants.MODIS_TILE_WIDTH/scaleFactor);
        final int height = (int) (SnapMultiplyConstants.MODIS_TILE_HEIGHT/scaleFactor);

        repro.setParameter("pixelSizeX", pixelSizeX);
        repro.setParameter("pixelSizeY", pixelSizeY);
        repro.setParameter("width", width);
        repro.setParameter("height", height);

        repro.setParameter("orthorectify", true);
        repro.setParameter("noDataValue", "NaN");
        repro.setSourceProduct(sourceProduct);
        return repro.getTargetProduct();
    }

    static Geometry computeProductGeometry(Product product) {
        try {
            final GeneralPath[] paths = ProductUtils.createGeoBoundaryPaths(product);
            final Polygon[] polygons = new Polygon[paths.length];
            final GeometryFactory factory = new GeometryFactory();
            for (int i = 0; i < paths.length; i++) {
                polygons[i] = convertAwtPathToJtsPolygon(paths[i], factory);
            }
            final DouglasPeuckerSimplifier peuckerSimplifier = new DouglasPeuckerSimplifier(
                    polygons.length == 1 ? polygons[0] : factory.createMultiPolygon(polygons));
            return peuckerSimplifier.getResultGeometry();
        } catch (Exception e) {
            return null;
        }
    }

    private static Polygon convertAwtPathToJtsPolygon(Path2D path, GeometryFactory factory) {
        final PathIterator pathIterator = path.getPathIterator(null);
        ArrayList<double[]> coordList = new ArrayList<>();
        int lastOpenIndex = 0;
        while (!pathIterator.isDone()) {
            final double[] coords = new double[6];
            final int segType = pathIterator.currentSegment(coords);
            if (segType == PathIterator.SEG_CLOSE) {
                // we should only detect a single SEG_CLOSE
                coordList.add(coordList.get(lastOpenIndex));
                lastOpenIndex = coordList.size();
            } else {
                coordList.add(coords);
            }
            pathIterator.next();
        }
        final Coordinate[] coordinates = new Coordinate[coordList.size()];
        for (int i1 = 0; i1 < coordinates.length; i1++) {
            final double[] coord = coordList.get(i1);
            coordinates[i1] = new Coordinate(coord[0], coord[1]);
        }

        return factory.createPolygon(factory.createLinearRing(coordinates), null);
    }

}
