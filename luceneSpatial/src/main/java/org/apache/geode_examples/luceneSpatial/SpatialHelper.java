/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode_examples.luceneSpatial;

import static org.locationtech.spatial4j.distance.DistanceUtils.EARTH_MEAN_RADIUS_MI;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.spatial.vector.PointVectorStrategy;
import org.locationtech.jts.algorithm.PointLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.impl.GeoCircle;
import org.locationtech.spatial4j.shape.impl.PointImpl;
import org.locationtech.spatial4j.shape.jts.JtsShapeFactory;

public class SpatialHelper {
  private static final SpatialContext CONTEXT = SpatialContext.GEO;
  private static final PointVectorStrategy STRATEGY =
      new PointVectorStrategy(CONTEXT, "location", PointVectorStrategy.DEFAULT_FIELDTYPE);

  /**
   * Return a lucene query that finds all points within the given radius from the given point
   */
  public static Query findWithin(double longitude, double latitude, double radiusMiles) {
    // Covert the radius in miles to a radius in degrees
    double radiusDEG = DistanceUtils.dist2Degrees(radiusMiles, EARTH_MEAN_RADIUS_MI);

    // Create a query that looks for all points within a circle around the given point
    SpatialArgs args = new SpatialArgs(SpatialOperation.IsWithin,
        new GeoCircle(createPoint(longitude, latitude), radiusDEG, CONTEXT));
    return STRATEGY.makeQuery(args);
  }

  /**
   * Return a list of fields that should be added to lucene document to index the given point
   */
  public static Field[] getIndexableFields(double longitude, double latitude) {
    Point point = createPoint(longitude, latitude);
    return STRATEGY.createIndexableFields(point);
  }

  private static Point createPoint(double longitude, double latitude) {
    return new PointImpl(longitude, latitude, CONTEXT);
  }

  public static Query findDistanceForTheGivenCoord(double sourceLang, double sourceLat,
      double radiusMiles) {
    double radiusDEG = DistanceUtils.dist2Degrees(radiusMiles, EARTH_MEAN_RADIUS_MI);
    SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
        new GeoCircle(createPoint(sourceLang, sourceLat), radiusDEG, CONTEXT));
    return STRATEGY.makeQuery(args);
  }


  public static Shape buildAPolygonFromTheCoordinates() {

    JtsSpatialContextFactory jtsSpatialContextFactory = new JtsSpatialContextFactory();
    JtsSpatialContext jtsSpatialContext = jtsSpatialContextFactory.newSpatialContext();
    JtsShapeFactory jtsShapeFactory = jtsSpatialContext.getShapeFactory();
    ShapeFactory.PolygonBuilder polygonBuilder = jtsShapeFactory.polygon();
    return polygonBuilder.pointXY(-23.543, -46.653).pointXY(-23.5346, -46.634)
        .pointXY(-23.543, -46.613).pointXY(-23.559, -46.614).pointXY(-23.567, -46.631)
        .pointXY(-23.560, -46.653).pointXY(-23.543, -46.653).build();
  }

  public static Query getPolygonQuery() {
    BooleanClause clause =
        new BooleanClause(new TermQuery(new Term(buildAPolygonFromTheCoordinates().toString())),
            BooleanClause.Occur.MUST);
    return clause.getQuery();
  }

  public static Query verifyLocationIsInsideShape(double givenLongitude, double givenLatitude,
      String[] latLong) {
    Geometry geometry =
        JtsSpatialContext.GEO.getShapeFactory().getGeometryFrom(getAShapeFromCoordinates(latLong));
    Coordinate givenCoordinate = new Coordinate(givenLatitude, givenLongitude);
    PointLocator pointLocator = new PointLocator();
    TermQuery termQuery =
        new TermQuery(new Term(String.valueOf(pointLocator.intersects(givenCoordinate, geometry))));
    BooleanClause clause = new BooleanClause(termQuery, BooleanClause.Occur.MUST);
    return clause.getQuery();
  }

  public static double computeArea(String[] latLong) {
    return SpatialHelper.getAShapeFromCoordinates(latLong).getArea(CONTEXT);
  }

  public static Field[] getIndexableFields() {
    Shape shape1 = buildAPolygonFromTheCoordinates();
    return STRATEGY.createIndexableFields(shape1);
  }

  public static Shape getAShapeFromCoordinates(String[] latLong) {
    JtsSpatialContextFactory jtsSpatialContextFactory = new JtsSpatialContextFactory();
    JtsSpatialContext jtsSpatialContext = jtsSpatialContextFactory.newSpatialContext();
    JtsShapeFactory jtsShapeFactory = jtsSpatialContext.getShapeFactory();
    ShapeFactory.PolygonBuilder polygonBuilder = jtsShapeFactory.polygon();
    return polygonBuilder.pointXY(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]))
        .pointXY((Double.parseDouble(latLong[2])), (Double.parseDouble(latLong[3])))
        .pointXY((Double.parseDouble(latLong[4])), (Double.parseDouble(latLong[5])))
        .pointXY((Double.parseDouble(latLong[6])), (Double.parseDouble(latLong[7])))
        .pointXY((Double.parseDouble(latLong[8])), (Double.parseDouble(latLong[9])))
        .pointXY((Double.parseDouble(latLong[10])), (Double.parseDouble(latLong[11])))
        .pointXY((Double.parseDouble(latLong[12])), (Double.parseDouble(latLong[13]))).build();
  }
}
