package org.swrlapi.drools.owl.dataranges;

import org.checkerframework.checker.nullness.qual.NonNull;

// TODO Datatype restriction data range not implemented

/**
 * Class representing an OWL datatype restriction data range
 *
 * @see org.semanticweb.owlapi.model.OWLDatatypeRestriction
 */
public class DTR implements DR
{
  private static final long serialVersionUID = 1L;

  @NonNull private final String rid;

  public DTR(@NonNull String rid)
  {
    this.rid = rid;
  }

  @NonNull @Override public String getdrid()
  {
    return this.rid;
  }
}
