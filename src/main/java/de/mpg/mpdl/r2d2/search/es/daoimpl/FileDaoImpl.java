package de.mpg.mpdl.r2d2.search.es.daoimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.r2d2.model.File;
import de.mpg.mpdl.r2d2.search.dao.FileDaoEs;

@Repository
public class FileDaoImpl extends ElasticSearchGenericDAOImpl<File> implements FileDaoEs {

  private static final Class<File> typeParameterClass = File.class;

  private static final String[] SOURCE_EXCLUSIONS = new String[] {};

  @Autowired
  private Environment env;

  public FileDaoImpl(Environment env) {
    super(env.getProperty("index.file.name"), typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }

}
