package fr.greta.soutenance2.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Service {
    protected final Logger log = LogManager.getLogger(getClass());
}
