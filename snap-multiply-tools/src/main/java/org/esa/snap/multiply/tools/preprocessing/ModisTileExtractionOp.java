package org.esa.snap.multiply.tools.preprocessing;

import com.vividsolutions.jts.geom.Geometry;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;
import org.esa.snap.core.gpf.annotations.OperatorMetadata;
import org.esa.snap.core.gpf.annotations.Parameter;
import org.esa.snap.core.gpf.annotations.SourceProduct;
import org.esa.snap.core.gpf.common.SubsetOp;

import java.awt.*;
import java.util.logging.Level;

/**
 * Subsets a source product onto the region represented by given MODIS SIN tile and
 * reprojects to corresponding MODIS Sinusoidal projection.
 *
 * @author Olaf Danne
 */
@OperatorMetadata(alias = "MULTIPLY.ModisTileExtraction", version = "0.82",
        authors = "O.Danne, T.Fincke",
        internal = true,
        category = "Optical/Preprocessing",
        copyright = "Copyright (C) 2019 by Brockmann Consult",
        description = "Subsets a source product onto the region represented by given MODIS SIN tile and " +
                "reprojects to corresponding MODIS Sinusoidal projection.")
public class ModisTileExtractionOp extends Operator {

    @Parameter(valueSet = {"500", "1000"}, defaultValue = "500",
            description = "MODIS tile resolution (500m or 1km).")
    private int modisTileResolution;

    @Parameter(description = "The MODIS SIN tile (hXXvYY).")
    private String tile;


    @SourceProduct(description = "Source product in satellite coordinates")
    private Product sourceProduct;


    @Override
    public void initialize() throws OperatorException {
        validateSourceProduct();
        final double scaleFactor = modisTileResolution/1000.0;
        final Product sinSourceProduct = TileExtractor.reprojectToModisTile(sourceProduct, tile, scaleFactor);
        Geometry geometry = TileExtractor.computeProductGeometry(sinSourceProduct);
        if (geometry == null) {
            throw new OperatorException("Could not get geometry for product");
        }
        final Rectangle region = SubsetOp.computePixelRegion(sourceProduct, geometry, 0);
        if (region.isEmpty()) {
            Product emptyProduct = new Product("Empty_" + sourceProduct.getName(), "EMPTY", 0, 0);
            String msg = "No intersection with source product boundary " + sourceProduct.getName();
            emptyProduct.setDescription(msg);
            getLogger().log(Level.WARNING, msg);
            setTargetProduct(emptyProduct);
        } else {
            setTargetProduct(sinSourceProduct);
        }
    }

    private void validateSourceProduct() {
        // todo
    }

    public static class Spi extends OperatorSpi {

        public Spi() {
            super(org.esa.snap.multiply.tools.preprocessing.ModisTileExtractionOp.class);
        }
    }
}
