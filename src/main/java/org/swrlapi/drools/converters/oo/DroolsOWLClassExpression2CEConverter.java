package org.swrlapi.drools.converters.oo;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.swrlapi.bridge.SWRLRuleEngineBridge;
import org.swrlapi.bridge.converters.TargetRuleEngineConverterBase;
import org.swrlapi.bridge.converters.TargetRuleEngineOWLClassExpressionConverter;
import org.swrlapi.drools.converters.id.DroolsOWLDataRange2IDConverter;
import org.swrlapi.drools.owl.classes.C;
import org.swrlapi.drools.owl.classes.CE;
import org.swrlapi.drools.owl.classes.DAVFCE;
import org.swrlapi.drools.owl.classes.DECCE;
import org.swrlapi.drools.owl.classes.DHVCE;
import org.swrlapi.drools.owl.classes.DMaxCCE;
import org.swrlapi.drools.owl.classes.DMinCCE;
import org.swrlapi.drools.owl.classes.DSVFCE;
import org.swrlapi.drools.owl.classes.OAVFCE;
import org.swrlapi.drools.owl.classes.OCOCE;
import org.swrlapi.drools.owl.classes.OECCE;
import org.swrlapi.drools.owl.classes.OHVCE;
import org.swrlapi.drools.owl.classes.OIOCE;
import org.swrlapi.drools.owl.classes.OMaxCCE;
import org.swrlapi.drools.owl.classes.OMinCCE;
import org.swrlapi.drools.owl.classes.OOHSCE;
import org.swrlapi.drools.owl.classes.OOOCE;
import org.swrlapi.drools.owl.classes.OSVFCE;
import org.swrlapi.drools.owl.classes.OUOCE;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DroolsOWLClassExpression2CEConverter extends TargetRuleEngineConverterBase
  implements TargetRuleEngineOWLClassExpressionConverter<CE>, OWLClassExpressionVisitorEx<CE>
{
  @NonNull private final DroolsOWLIndividual2IConverter droolsOWLIndividual2IConverter;
  @NonNull private final DroolsOWLPropertyExpression2PEConverter droolsOWLPropertyExpression2PEConverter;
  @NonNull private final DroolsOWLDataRange2IDConverter droolsOWLDataRange2IDConverter;
  @NonNull private final DroolsOWLLiteral2LConverter droolsOWLLiteral2LConverter;

  private final Map<OWLObjectIntersectionOf, OIOCE> oioces = new HashMap<>();
  private final Map<OWLObjectUnionOf, OUOCE> ouoces = new HashMap<>();
  private final Map<OWLObjectComplementOf, OCOCE> ococes = new HashMap<>();
  private final Map<OWLObjectSomeValuesFrom, OSVFCE> osvfces = new HashMap<>();
  private final Map<OWLObjectAllValuesFrom, OAVFCE> oavfces = new HashMap<>();
  private final Map<OWLObjectHasValue, OHVCE> ohvces = new HashMap<>();
  private final Map<OWLObjectExactCardinality, OECCE> oecces = new HashMap<>();
  private final Map<OWLObjectMinCardinality, OMinCCE> omincces = new HashMap<>();
  private final Map<OWLObjectMaxCardinality, OMaxCCE> omaxcces = new HashMap<>();
  private final Map<OWLObjectHasSelf, OOHSCE> oohsces = new HashMap<>();
  private final Map<OWLObjectOneOf, OOOCE> oooces = new HashMap<>();
  private final Map<OWLDataSomeValuesFrom, DSVFCE> dsvfces = new HashMap<>();
  private final Map<OWLDataAllValuesFrom, DAVFCE> davfces = new HashMap<>();
  private final Map<OWLDataHasValue, DHVCE> dhvces = new HashMap<>();
  private final Map<OWLDataExactCardinality, DECCE> decces = new HashMap<>();
  private final Map<OWLDataMinCardinality, DMinCCE> dmincces = new HashMap<>();
  private final Map<OWLDataMaxCardinality, DMaxCCE> dmaxcces = new HashMap<>();

  private final Set<CE> ces = new HashSet<>();

  private int classExpressionIndex = 0;

  public DroolsOWLClassExpression2CEConverter(@NonNull SWRLRuleEngineBridge bridge,
    @NonNull DroolsOWLIndividual2IConverter droolsOWLIndividual2IConverter,
    @NonNull DroolsOWLPropertyExpression2PEConverter droolsOWLPropertyExpression2PEConverter,
    @NonNull DroolsOWLDataRange2IDConverter droolsOWLDataRange2IDConverter,
    @NonNull DroolsOWLLiteral2LConverter droolsOWLLiteral2LConverter)
  {
    super(bridge);
    this.droolsOWLIndividual2IConverter = droolsOWLIndividual2IConverter;
    this.droolsOWLPropertyExpression2PEConverter = droolsOWLPropertyExpression2PEConverter;
    this.droolsOWLDataRange2IDConverter = droolsOWLDataRange2IDConverter;
    this.droolsOWLLiteral2LConverter = droolsOWLLiteral2LConverter;
  }

  @NonNull @Override public CE convert(@NonNull OWLClassExpression classExpression)
  {
    return classExpression.accept(this);
  }

  public void reset()
  {
    this.classExpressionIndex = 0;

    this.oioces.clear();
    this.ouoces.clear();
    this.ococes.clear();
    this.osvfces.clear();
    this.oavfces.clear();
    this.ohvces.clear();
    this.oecces.clear();
    this.omincces.clear();
    this.omaxcces.clear();
    this.oohsces.clear();
    this.oooces.clear();
    this.dsvfces.clear();
    this.davfces.clear();
    this.dhvces.clear();
    this.decces.clear();
    this.dmincces.clear();
    this.dmaxcces.clear();

    this.ces.clear();
  }

  @NonNull @Override public C visit(@NonNull OWLClass cls)
  {
    String classPrefixedName = iri2PrefixedName(cls.getIRI());

    return new C(classPrefixedName);
  }

  @NonNull @Override public OIOCE visit(@NonNull OWLObjectIntersectionOf objectIntersectionOf)
  {
    if (oioces.containsKey(objectIntersectionOf))
      return oioces.get(objectIntersectionOf);
    else {
      String classExpressionID = generateCEID();
      Set<String> classIDs = new HashSet<>();
      for (OWLClassExpression ce : objectIntersectionOf.getOperands()) {
        String classID = convert(ce).getceid();
        classIDs.add(classID);
      }
      OIOCE oioce = new OIOCE(classExpressionID, classIDs);
      oioces.put(objectIntersectionOf, oioce);
      ces.add(oioce);

      return oioce;
    }
  }

  @NonNull @Override public OUOCE visit(@NonNull OWLObjectUnionOf objectUnionOf)
  {
    if (ouoces.containsKey(objectUnionOf))
      return ouoces.get(objectUnionOf);
    else {
      String classExpressionID = generateCEID();

      Set<@NonNull String> classExpressionIDs = new HashSet<>();
      for (OWLClassExpression ce : objectUnionOf.getOperands()) {
        String cid = convert(ce).getceid();
        classExpressionIDs.add(cid);
      }
      OUOCE ouoce = new OUOCE(classExpressionID, classExpressionIDs);
      ouoces.put(objectUnionOf, ouoce);
      ces.add(ouoce);

      return ouoce;
    }
  }

  @Override public @NonNull OCOCE visit(@NonNull OWLObjectComplementOf objectComplementOf)
  {
    if (ococes.containsKey(objectComplementOf))
      return ococes.get(objectComplementOf);
    else {
      String classExpressionID = generateCEID();

      String complementClassID = convert(objectComplementOf.getOperand()).getceid();
      OCOCE ococe = new OCOCE(classExpressionID, complementClassID);
      ococes.put(objectComplementOf, ococe);
      ces.add(ococe);

      return ococe;
    }
  }

  @NonNull @Override public OSVFCE visit(@NonNull OWLObjectSomeValuesFrom objectSomeValuesFrom)
  {
    if (osvfces.containsKey(objectSomeValuesFrom))
      return osvfces.get(objectSomeValuesFrom);
    else {
      String classExpressionID = generateCEID();

      String someValuesFromClassID = convert(objectSomeValuesFrom.getFiller()).getceid();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectSomeValuesFrom.getProperty())
        .getid();
      OSVFCE osvfce = new OSVFCE(classExpressionID, propertyID, someValuesFromClassID);
      osvfces.put(objectSomeValuesFrom, osvfce);
      ces.add(osvfce);

      return osvfce;
    }
  }

  @NonNull @Override public OAVFCE visit(@NonNull OWLObjectAllValuesFrom objectAllValuesFrom)
  {
    if (oavfces.containsKey(objectAllValuesFrom))
      return oavfces.get(objectAllValuesFrom);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectAllValuesFrom.getProperty())
        .getid();
      String allValuesFromClassID = convert(objectAllValuesFrom.getFiller()).getceid();
      OAVFCE oavfce = new OAVFCE(classExpressionID, propertyID, allValuesFromClassID);
      oavfces.put(objectAllValuesFrom, oavfce);
      ces.add(oavfce);

      return oavfce;
    }
  }

  @NonNull @Override public OHVCE visit(@NonNull OWLObjectHasValue objectHasValue)
  {
    if (ohvces.containsKey(objectHasValue))
      return ohvces.get(objectHasValue);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectHasValue.getProperty()).getid();
      String fillerIndividualID = getDroolsOWLIndividual2IConverter().convert(objectHasValue.getFiller()).getid();
      OHVCE ohvce = new OHVCE(classExpressionID, propertyID, fillerIndividualID);
      ohvces.put(objectHasValue, ohvce);
      ces.add(ohvce);

      return ohvce;
    }
  }

  @Override public @NonNull OECCE visit(@NonNull OWLObjectExactCardinality objectExactCardinality)
  {
    if (oecces.containsKey(objectExactCardinality))
      return oecces.get(objectExactCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectExactCardinality.getProperty())
        .getid();
      int cardinality = objectExactCardinality.getCardinality();
      OECCE oecce = new OECCE(classExpressionID, propertyID, cardinality);
      oecces.put(objectExactCardinality, oecce);
      ces.add(oecce);

      return oecce;
    }
  }

  @NonNull @Override public OMinCCE visit(@NonNull OWLObjectMinCardinality objectMinCardinality)
  {
    if (omincces.containsKey(objectMinCardinality))
      return omincces.get(objectMinCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectMinCardinality.getProperty())
        .getid();
      int cardinality = objectMinCardinality.getCardinality();
      OMinCCE omincce = new OMinCCE(classExpressionID, propertyID, cardinality);
      omincces.put(objectMinCardinality, omincce);
      ces.add(omincce);

      return omincce;
    }
  }

  @NonNull @Override public OMaxCCE visit(@NonNull OWLObjectMaxCardinality objectMaxCardinality)
  {
    if (omaxcces.containsKey(objectMaxCardinality))
      return omaxcces.get(objectMaxCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectMaxCardinality.getProperty())
        .getid();
      int cardinality = objectMaxCardinality.getCardinality();
      OMaxCCE omaxcce = new OMaxCCE(classExpressionID, propertyID, cardinality);
      omaxcces.put(objectMaxCardinality, omaxcce);
      ces.add(omaxcce);

      return omaxcce;
    }
  }

  @Override public @NonNull OOHSCE visit(@NonNull OWLObjectHasSelf objectHasSelf)
  {
    if (oohsces.containsKey(objectHasSelf))
      return oohsces.get(objectHasSelf);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(objectHasSelf.getProperty()).getid();
      OOHSCE oohsce = new OOHSCE(classExpressionID, propertyID);
      oohsces.put(objectHasSelf, oohsce);
      ces.add(oohsce);

      return oohsce;
    }
  }

  @NonNull @Override public OOOCE visit(@NonNull OWLObjectOneOf objectOneOf)
  {
    if (oooces.containsKey(objectOneOf))
      return oooces.get(objectOneOf);
    else {
      String classExpressionID = generateCEID();
      Set<@NonNull String> individualIDs = new HashSet<>();
      for (OWLIndividual individual : objectOneOf.getIndividuals()) {
        String individualID = getDroolsOWLIndividual2IConverter().convert(individual).getid();
        individualIDs.add(individualID);
      }
      OOOCE oooce = new OOOCE(classExpressionID, individualIDs);
      oooces.put(objectOneOf, oooce);
      ces.add(oooce);

      return oooce;
    }
  }

  @NonNull @Override public DSVFCE visit(@NonNull OWLDataSomeValuesFrom dataSomeValuesFrom)
  {
    if (dsvfces.containsKey(dataSomeValuesFrom))
      return dsvfces.get(dataSomeValuesFrom);
    else {
      String classExpressionID = generateCEID();
      String someValuesFromDataRangeID = getDroolsOWLDataRange2IDConverter().convert(dataSomeValuesFrom.getFiller());
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataSomeValuesFrom.getProperty())
        .getid();
      DSVFCE dsvfce = new DSVFCE(classExpressionID, propertyID, someValuesFromDataRangeID);
      dsvfces.put(dataSomeValuesFrom, dsvfce);
      ces.add(dsvfce);

      return dsvfce;
    }
  }

  @NonNull @Override public DAVFCE visit(@NonNull OWLDataAllValuesFrom dataAllValuesFrom)
  {
    if (davfces.containsKey(dataAllValuesFrom))
      return davfces.get(dataAllValuesFrom);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataAllValuesFrom.getProperty()).getid();
      String allValuesFromDataRangeID = getDroolsOWLDataRange2IDConverter().convert(dataAllValuesFrom.getFiller());
      DAVFCE davfce = new DAVFCE(classExpressionID, propertyID, allValuesFromDataRangeID);
      davfces.put(dataAllValuesFrom, davfce);
      ces.add(davfce);

      return davfce;
    }
  }

  @NonNull @Override public DHVCE visit(@NonNull OWLDataHasValue dataHasValue)
  {
    if (dhvces.containsKey(dataHasValue))
      return dhvces.get(dataHasValue);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataHasValue.getProperty()).getid();
      OWLLiteral fillerLiteral = dataHasValue.getFiller();
      DHVCE dhvce = new DHVCE(classExpressionID, propertyID, getDroolsOWLLiteral2LConverter().convert(fillerLiteral));
      dhvces.put(dataHasValue, dhvce);
      ces.add(dhvce);

      return dhvce;
    }
  }

  @NonNull @Override public DMinCCE visit(@NonNull OWLDataMinCardinality dataMinCardinality)
  {
    if (dmincces.containsKey(dataMinCardinality))
      return dmincces.get(dataMinCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataMinCardinality.getProperty())
        .getid();
      int cardinality = dataMinCardinality.getCardinality();
      DMinCCE dmincce = new DMinCCE(classExpressionID, propertyID, cardinality);
      dmincces.put(dataMinCardinality, dmincce);
      ces.add(dmincce);

      return dmincce;
    }
  }

  @NonNull @Override public DECCE visit(@NonNull OWLDataExactCardinality dataExactCardinality)
  {
    if (decces.containsKey(dataExactCardinality))
      return decces.get(dataExactCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataExactCardinality.getProperty())
        .getid();
      int cardinality = dataExactCardinality.getCardinality();
      DECCE decce = new DECCE(classExpressionID, propertyID, cardinality);
      decces.put(dataExactCardinality, decce);
      ces.add(decce);

      return decce;
    }
  }

  @NonNull @Override public DMaxCCE visit(@NonNull OWLDataMaxCardinality dataMaxCardinality)
  {
    if (dmaxcces.containsKey(dataMaxCardinality))
      return dmaxcces.get(dataMaxCardinality);
    else {
      String classExpressionID = generateCEID();
      String propertyID = getDroolsOWLPropertyExpression2PEConverter().convert(dataMaxCardinality.getProperty())
        .getid();
      int cardinality = dataMaxCardinality.getCardinality();
      DMaxCCE dmaxcce = new DMaxCCE(classExpressionID, propertyID, cardinality);
      dmaxcces.put(dataMaxCardinality, dmaxcce);
      ces.add(dmaxcce);

      return dmaxcce;
    }
  }

  @NonNull @Override public C convert(OWLClass cls)
  {
    return visit(cls);
  }

  @NonNull @Override public OOOCE convert(OWLObjectOneOf objectOneOf)
  {
    return visit(objectOneOf);
  }

  @NonNull @Override public OIOCE convert(OWLObjectIntersectionOf objectIntersectionOf)
  {
    return visit(objectIntersectionOf);
  }

  @NonNull @Override public OUOCE convert(OWLObjectUnionOf objectUnionOf)
  {
    return visit(objectUnionOf);
  }

  @NonNull @Override public OSVFCE convert(OWLObjectSomeValuesFrom objectSomeValuesFrom)
  {
    return visit(objectSomeValuesFrom);
  }

  @Override public @NonNull OCOCE convert(OWLObjectComplementOf objectComplementOf)
  {
    return visit(objectComplementOf);
  }

  @NonNull @Override public DSVFCE convert(OWLDataSomeValuesFrom dataSomeValuesFrom)
  {
    return visit(dataSomeValuesFrom);
  }

  @Override public @NonNull DECCE convert(OWLDataExactCardinality dataExactCardinality)
  {
    return visit(dataExactCardinality);
  }

  @Override public @NonNull OECCE convert(OWLObjectExactCardinality objectExactCardinality)
  {
    return visit(objectExactCardinality);
  }

  @NonNull @Override public DMinCCE convert(OWLDataMinCardinality dataMinCardinality)
  {
    return visit(dataMinCardinality);
  }

  @NonNull @Override public OMinCCE convert(OWLObjectMinCardinality objectMinCardinality)
  {
    return visit(objectMinCardinality);
  }

  @NonNull @Override public DMaxCCE convert(OWLDataMaxCardinality dataMaxCardinality)
  {
    return visit(dataMaxCardinality);
  }

  @NonNull @Override public OMaxCCE convert(OWLObjectMaxCardinality objectMaxCardinality)
  {
    return visit(objectMaxCardinality);
  }

  @NonNull @Override public DHVCE convert(OWLDataHasValue dataHasValue)
  {
    return visit(dataHasValue);
  }

  @NonNull @Override public OHVCE convert(OWLObjectHasValue objectHasValue)
  {
    return visit(objectHasValue);
  }

  @NonNull @Override public OAVFCE convert(OWLObjectAllValuesFrom objectAllValuesFrom)
  {
    return visit(objectAllValuesFrom);
  }

  @NonNull @Override public DAVFCE convert(OWLDataAllValuesFrom dataAllValuesFrom)
  {
    return visit(dataAllValuesFrom);
  }

  @Override public @NonNull OOHSCE convert(OWLObjectHasSelf objectHasSelf)
  {
    return visit(objectHasSelf);
  }

  @NonNull public Set<@NonNull CE> getCEs()
  {
    return Collections.unmodifiableSet(this.ces);
  }

  @NonNull private String generateCEID()
  {
    return "CEID" + this.classExpressionIndex++;
  }

  @NonNull private DroolsOWLIndividual2IConverter getDroolsOWLIndividual2IConverter()
  {
    return this.droolsOWLIndividual2IConverter;
  }

  private @NonNull DroolsOWLPropertyExpression2PEConverter getDroolsOWLPropertyExpression2PEConverter()
  {
    return this.droolsOWLPropertyExpression2PEConverter;
  }

  @NonNull private DroolsOWLLiteral2LConverter getDroolsOWLLiteral2LConverter()
  {
    return this.droolsOWLLiteral2LConverter;
  }

  @NonNull private DroolsOWLDataRange2IDConverter getDroolsOWLDataRange2IDConverter()
  {
    return this.droolsOWLDataRange2IDConverter;
  }
}
