/*
 *   Copyright 2018 . AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.elb.dashboard;

import com.appdynamics.extensions.conf.ControllerInfo;
import com.appdynamics.extensions.dashboard.CustomDashboardJsonUploader;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;

import static com.appdynamics.extensions.aws.elb.dashboard.DashboardConstants.*;


/**
 * Created by bhuvnesh.kumar on 7/5/18.
 */
public class Dashboard {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(Dashboard.class);

    private Map config;
    private String dashboardString;

    private ControllerInfo controllerInfo;
    private CustomDashboardJsonUploader customDashboardJsonUploader;

    public Dashboard(Map config, CustomDashboardJsonUploader customDashboardJsonUploader, ControllerInfo controllerInfo) {

        this.config = config;
        this.customDashboardJsonUploader = customDashboardJsonUploader;
        this.controllerInfo = controllerInfo;
        LOGGER.debug("Leaving Dashboard Class");

    }

    public void sendDashboard() {
        try {
            controllerInfo = controllerInfo.getControllerInfo();
            Map<String, ? super Object> argsMap = ConnectionProperties.getArgumentMap(controllerInfo, config);
            if (config.get(ENALBED).toString().equals(TRUE)) {
                uploadDashboard(argsMap);
            } else {
                LOGGER.debug("Upload dashboard disabled, not uploading dashboard.");
            }

        } catch (Exception e) {
            LOGGER.error("Unable to upload dashboard", e);
        }
    }


    // TODO use the threads that we have in our commons library, figure it out. should not be directly runnable but amonitorrunnable

    private void uploadDashboard(Map<String, ? super Object> argsMap) {
        LOGGER.debug("Attempting to upload dashboard.");

        loadDashboardBasedOnSim();
        dashboardString = ReplaceDefaultInfo.replaceFields(dashboardString, controllerInfo, config);
//        customDashboardJsonUploader.uploadDashboard(config.get(DASHBOARD_NAME).toString(), dashboardString, argsMap, false);

        LOGGER.debug("done with uploadDashboard()");
    }

    private void loadDashboardBasedOnSim() {
        LOGGER.debug("Sim Enabled: {}", controllerInfo.getSimEnabled());

        if (controllerInfo.getSimEnabled() == false) {
            try {
                LOGGER.debug("Getting the Normal Dashboard File");
                dashboardString = FileUtils.readFileToString(new File(config.get("pathToNormalDashboard").toString()));
            } catch (Exception e) {
                LOGGER.info("Unable to load Normal Dashboard File at Path: {}", config.get("pathToNormalDashboard").toString());
            }
        } else {
            try {
                LOGGER.debug("Getting the SIM Dashboard File");

                dashboardString = FileUtils.readFileToString(new File(config.get("pathToSIMDashboard").toString()));
            } catch (Exception e) {
                LOGGER.info("Unable to load SIM Dashboard File at Path: {}", config.get("pathToSIMDashboard").toString());
            }
        }
    }

}


