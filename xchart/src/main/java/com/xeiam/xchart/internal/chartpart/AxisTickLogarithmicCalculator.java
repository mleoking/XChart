/**
 * Copyright 2011 - 2014 Xeiam LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xeiam.xchart.internal.chartpart;

import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.internal.Utils;
import com.xeiam.xchart.internal.chartpart.Axis.Direction;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis ticks for logarithmic axes
 * 
 * @author timmolter
 */
public class AxisTickLogarithmicCalculator extends AxisTickCalculator {

  NumberFormatter numberFormatter = null;

  /**
   * Constructor
   * 
   * @param axisDirection
   * @param workingSpace
   * @param minValue
   * @param maxValue
   * @param styleManager
   */
  public AxisTickLogarithmicCalculator(Direction axisDirection, double workingSpace, double minValue, double maxValue, StyleManager styleManager) {

    super(axisDirection, workingSpace, minValue, maxValue, styleManager);
    numberFormatter = new NumberFormatter(styleManager);
    calculate();
  }

  private void calculate() {

    // a check if all axis data are the exact same values
    if (minValue == maxValue) {
      tickLabels.add(numberFormatter.formatNumber(maxValue, minValue, maxValue));
      tickLocations.add(workingSpace / 2.0);
      return;
    }

    // tick space - a percentage of the working space available for ticks
    double tickSpace = styleManager.getAxisTickSpaceRatio() * workingSpace; // in plot space

    // where the tick should begin in the working space in pixels
    double margin = Utils.getTickStartOffset(workingSpace, tickSpace); // in plot space double gridStep = getGridStepForDecimal(tickSpace);

    int logMin = (int) Math.floor(Math.log10(minValue));
    int logMax = (int) Math.ceil(Math.log10(maxValue));
    // int logMin = (int) Math.log10(minValue.doubleValue());
    // int logMax = (int) Math.log10(maxValue.doubleValue());
    // System.out.println("minValue: " + minValue);
    // System.out.println("maxValue: " + maxValue);
    // System.out.println("logMin: " + logMin);
    // System.out.println("logMax: " + logMax);

    if (axisDirection == Direction.Y && styleManager.getYAxisMin() != null) {
      logMin = (int) (Math.log10(styleManager.getYAxisMin())); // no floor
    }
    if (axisDirection == Direction.Y && styleManager.getYAxisMax() != null) {
      logMax = (int) (Math.log10(styleManager.getYAxisMax())); // no floor
    }
    if (axisDirection == Direction.X && styleManager.getXAxisMin() != null) {
      logMin = (int) (Math.log10(styleManager.getXAxisMin())); // no floor
    }
    if (axisDirection == Direction.X && styleManager.getXAxisMax() != null) {
      logMax = (int) (Math.log10(styleManager.getXAxisMax())); // no floor
    }

    // double firstPosition = getFirstPosition(tickStep);
    // System.out.println("firstPosition: " + firstPosition);
    double firstPosition = Utils.pow(10, logMin);
    double tickStep = Utils.pow(10, logMin - 1);

    for (int i = logMin; i <= logMax; i++) { // for each decade

      // System.out.println("tickStep: " + tickStep);
      // System.out.println("firstPosition: " + firstPosition);
      // System.out.println("i: " + i);
      // System.out.println("pow(10, i).doubleValue(): " + pow(10, i).doubleValue());

      // using the .00000001 factor to deal with double value imprecision
      for (double j = firstPosition; j <= Utils.pow(10, i) + .00000001; j = j + tickStep) {

        // System.out.println("j: " + j);
        // System.out.println(Math.log10(j) % 1);

        if (j < minValue - tickStep * .2) {
          // System.out.println("continue");
          continue;
        }

        if (j > maxValue + tickStep * 1.2) {
          // System.out.println("break");
          break;
        }

        // only add labels for the decades
        if (Math.abs(Math.log10(j) % 1) < 0.00000001) {
          tickLabels.add(numberFormatter.formatLogNumber(j));
        }
        else {
          tickLabels.add(null);
        }

        // add all the tick marks though
        double tickLabelPosition = (int) (margin + (Math.log10(j) - Math.log10(minValue)) / (Math.log10(maxValue) - Math.log10(minValue)) * tickSpace);
        tickLocations.add(tickLabelPosition);
      }
      tickStep = tickStep * Utils.pow(10, 1);
      firstPosition = tickStep + Utils.pow(10, i);
    }
  }
}
