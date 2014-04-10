package org.swrlapi.drools.converters;

import java.util.Set;

import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.swrlapi.converters.TargetRuleEngineSWRLBodyAtomWithVariableNamesConverter;
import org.swrlapi.core.SWRLRuleEngineBridge;
import org.swrlapi.core.arguments.SWRLBuiltInArgument;
import org.swrlapi.drools.DroolsNames;
import org.swrlapi.drools.DroolsSWRLBuiltInInvoker;
import org.swrlapi.drools.sqwrl.VPATH;
import org.swrlapi.drools.swrl.BAP;
import org.swrlapi.drools.swrl.BAVNs;
import org.swrlapi.exceptions.TargetRuleEngineException;
import org.swrlapi.exceptions.TargetRuleEngineNotImplementedFeatureException;
import org.swrlapi.ext.SWRLAPIBuiltInAtom;

/**
 * This class converts SWRL body atoms to a their DRL representation for use in rules.
 * <p>
 * Head and body atoms are converted differently - hence the need for two converters. Body atom converters must also
 * know the variables defined by previous atoms because a different syntax is required in DRL for declaring a variable
 * vs. referring to one that is already declared. In the head, all variables are guaranteed to have already been
 * declared in SWRL.
 * 
 * @see SWRLAtom
 */
public class DroolsSWRLBodyAtom2DRLConverter extends DroolsConverterBase implements
		TargetRuleEngineSWRLBodyAtomWithVariableNamesConverter<String>
{
	private final DroolsSWRLBodyAtomArgument2DRLConverter bodyAtomArgumentConverter;
	private final DroolsSWRLBuiltInArgument2DRLConverter builtInArgumentConverter;
	private final DroolsOWLPropertyExpressionConverter propertyExpressionConverter;
	private final DroolsOWLClassExpressionConverter classExpressionConverter;

	private int builtInIndexInBody; // Each built-in atom in the body gets a unique index, starting at 0

	public DroolsSWRLBodyAtom2DRLConverter(SWRLRuleEngineBridge bridge)
	{
		super(bridge);

		this.bodyAtomArgumentConverter = new DroolsSWRLBodyAtomArgument2DRLConverter(bridge);
		this.builtInArgumentConverter = new DroolsSWRLBuiltInArgument2DRLConverter(bridge);
		this.propertyExpressionConverter = new DroolsOWLPropertyExpressionConverter(bridge);
		this.classExpressionConverter = new DroolsOWLClassExpressionConverter(bridge);
		this.builtInIndexInBody = 0;
	}

	public void reset()
	{
		this.builtInIndexInBody = 0;
		this.propertyExpressionConverter.reset();
	}

	public String convert(SWRLAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{ // TODO Visitor to replace instanceof
		if (atom instanceof SWRLDataRangeAtom) {
			return convert((SWRLDataRangeAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLClassAtom) {
			return convert((SWRLClassAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLDataPropertyAtom) {
			return convert((SWRLDataPropertyAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLObjectPropertyAtom) {
			return convert((SWRLObjectPropertyAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLSameIndividualAtom) {
			return convert((SWRLSameIndividualAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLDifferentIndividualsAtom) {
			return convert((SWRLDifferentIndividualsAtom)atom, variableShortNames);
		} else if (atom instanceof SWRLAPIBuiltInAtom) {
			return convert((SWRLAPIBuiltInAtom)atom, variableShortNames);
		} else
			throw new RuntimeException("unknown SWRL atom type " + atom.getClass().getCanonicalName());
	}

	@Override
	public String convert(SWRLDataRangeAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{
		throw new TargetRuleEngineNotImplementedFeatureException("data range atoms not implemented in rule body");
	}

	public String convert(SWRLDataRangeAtom atom) throws TargetRuleEngineException
	{
		throw new TargetRuleEngineNotImplementedFeatureException("data range atoms not implemented in rule head");
	}

	@Override
	public String convert(SWRLClassAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{
		String classID = getOWLClassExpressionConverter().convert(atom.getPredicate());
		SWRLIArgument argument = atom.getArgument();
		String representation = DroolsNames.CLASS_ASSERTION_AXIOM_CLASS_NAME + "(" + DroolsNames.CLASS_FIELD_NAME + "=="
				+ addQuotes(classID) + ", ";

		representation += getSWRLBodyAtomArgumentConverter().convert(argument, DroolsNames.INDIVIDUAL_FIELD_NAME,
				variableShortNames);
		representation += ")";

		return representation;
	}

	@Override
	public String convert(SWRLDataPropertyAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{
		String propertyID = getOWLPropertyExpressionConverter().convert(atom.getPredicate());
		SWRLIArgument argument1 = atom.getFirstArgument();
		SWRLDArgument argument2 = atom.getSecondArgument();
		String representation = DroolsNames.DATA_PROPERTY_ASSERTION_AXIOM_CLASS_NAME + "(";

		representation += getSWRLBodyAtomArgumentConverter().convert(argument1, DroolsNames.SUBJECT_FIELD_NAME,
				variableShortNames);
		representation += ", " + DroolsNames.PROPERTY_FIELD_NAME + "." + DroolsNames.ID_FIELD_NAME + "=="
				+ addQuotes(propertyID) + ", ";
		representation += getSWRLBodyAtomArgumentConverter().convert(argument2, DroolsNames.OBJECT_FIELD_NAME,
				variableShortNames);
		representation += ")";

		return representation;
	}

	@Override
	public String convert(SWRLObjectPropertyAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{
		String propertyID = getOWLPropertyExpressionConverter().convert(atom.getPredicate());
		SWRLIArgument argument1 = atom.getFirstArgument();
		SWRLIArgument argument2 = atom.getSecondArgument();
		String representation = DroolsNames.OBJECT_PROPERTY_ASSERTION_AXIOM_CLASS_NAME + "(";

		representation += getSWRLBodyAtomArgumentConverter().convert(argument1, DroolsNames.SUBJECT_FIELD_NAME,
				variableShortNames);
		representation += ", " + DroolsNames.PROPERTY_FIELD_NAME + "." + DroolsNames.ID_FIELD_NAME + "=="
				+ addQuotes(propertyID) + ", ";
		representation += getSWRLBodyAtomArgumentConverter().convert(argument2, DroolsNames.OBJECT_FIELD_NAME,
				variableShortNames);
		representation += ")";

		return representation;
	}

	@Override
	public String convert(SWRLSameIndividualAtom atom, Set<String> variableShortNames) throws TargetRuleEngineException
	{
		SWRLIArgument argument1 = atom.getFirstArgument();
		SWRLIArgument argument2 = atom.getSecondArgument();
		String representation = DroolsNames.SAME_INDIVIDUAL_AXIOM_CLASS_NAME + "(";

		representation += getSWRLBodyAtomArgumentConverter().convert(argument1, DroolsNames.INDIVIDUAL_1_FIELD_NAME,
				variableShortNames);
		representation += ", ";
		representation += getSWRLBodyAtomArgumentConverter().convert(argument2, DroolsNames.INDIVIDUAL_2_FIELD_NAME,
				variableShortNames);
		representation += ")";

		return representation;
	}

	@Override
	public String convert(SWRLDifferentIndividualsAtom atom, Set<String> variableShortNames)
			throws TargetRuleEngineException
	{
		SWRLIArgument argument1 = atom.getFirstArgument();
		SWRLIArgument argument2 = atom.getSecondArgument();
		String representation = DroolsNames.DIFFERENT_INDIVIDUALS_AXIOM_CLASS_NAME + "(";

		representation += getSWRLBodyAtomArgumentConverter().convert(argument1, DroolsNames.INDIVIDUAL_1_FIELD_NAME,
				variableShortNames);
		representation += ", ";
		representation += getSWRLBodyAtomArgumentConverter().convert(argument2, DroolsNames.INDIVIDUAL_2_FIELD_NAME,
				variableShortNames);
		representation += ")";

		return representation;
	}

	@Override
	public String convert(SWRLAPIBuiltInAtom builtInAtom, Set<String> variableShortNames)
			throws TargetRuleEngineException
	{
		String builtInName = builtInAtom.getBuiltInShortName();
		String ruleName = builtInAtom.getRuleName();
		boolean variableArgumentEncountered = false;
		String representation = DroolsNames.BUILT_IN_ARGUMENTS_PATTERN_CLASS_NAME + "(";
		boolean isFirst;

		int argumentNumber = 1;
		for (SWRLBuiltInArgument argument : builtInAtom.getBuiltInArguments()) {
			if (argument.isVariable()) {
				String variableShortName = getDroolsSWRLVariableConverter().swrlVariable2VariableName(argument.asVariable());
				if (variableArgumentEncountered)
					representation += ", ";
				representation += variableShortName2DRL(variableShortName,
						DroolsNames.BUILT_IN_ARGUMENT_PATTERN_FIELD_NAME_PREFIX + argumentNumber, variableShortNames);
				variableArgumentEncountered = true;
			}
			argumentNumber++;
			if (argumentNumber > BAP.MaxArguments)
				throw new TargetRuleEngineException("a maximum of " + BAP.MaxArguments
						+ " built-in arguments are currently supported by Drools");
		}

		representation += ") from invoker.invoke(\"" + ruleName + "\", \"" + builtInName + "\", " + this.builtInIndexInBody
				+ ", false, ";

		if (builtInAtom.getPathVariableShortNames().size() > VPATH.MaxArguments)
			throw new TargetRuleEngineException("a maximum of " + VPATH.MaxArguments
					+ " built-in arguments are currently supported by Drools");

		isFirst = true;
		representation += "new " + DroolsNames.BUILT_IN_VARIABLE_PATH_CLASS_NAME + "(";
		isFirst = true;
		for (String variableShortName : builtInAtom.getPathVariableShortNames()) {
			if (!isFirst)
				representation += ", ";
			representation += getDroolsSWRLVariableConverter().variableShortName2DRL(variableShortName);
			isFirst = false;
		}
		representation += "), ";

		if (builtInAtom.getNumberOfArguments() > BAVNs.MaxArguments) // TODO fix with BAVNs varargs Drools fix
			throw new TargetRuleEngineException("a maximum of " + BAVNs.MaxArguments
					+ " built-in arguments are currently supported by Drools");

		representation += "new " + DroolsNames.BUILT_IN_VARIABLE_NAMES_CLASS_NAME + "(";
		isFirst = true;
		for (SWRLBuiltInArgument argument : builtInAtom.getBuiltInArguments()) {
			if (!isFirst)
				representation += ", ";
			if (argument.isVariable())
				representation += "\"" + getDroolsSWRLVariableConverter().swrlVariable2VariableName(argument.asVariable())
						+ "\"";
			else
				representation += "\"\"";
			isFirst = false;
		}
		representation += "), ";

		if (builtInAtom.getNumberOfArguments() > DroolsSWRLBuiltInInvoker.MaxBuiltInArguments)
			throw new TargetRuleEngineException("a maximum of " + DroolsSWRLBuiltInInvoker.MaxBuiltInArguments
					+ " can be passed to built-ins");

		isFirst = true;
		for (SWRLBuiltInArgument argument : builtInAtom.getBuiltInArguments()) {
			if (!isFirst)
				representation += ", ";
			representation += getSWRLBuiltInArgumentConverter().convert(argument);
			isFirst = false;
		}

		representation += ")";

		this.builtInIndexInBody++;

		return representation;
	}

	private String variableShortName2DRL(String variableShortName, String fieldName, Set<String> variableShortNames)
	{
		if (variableShortNames.contains(variableShortName)) {
			return fieldName + "==" + getDroolsSWRLVariableConverter().variableShortName2DRL(variableShortName);
		} else {
			variableShortNames.add(variableShortName);
			return getDroolsSWRLVariableConverter().variableShortName2DRL(variableShortName) + ":" + fieldName;
		}
	}

	private DroolsSWRLBodyAtomArgument2DRLConverter getSWRLBodyAtomArgumentConverter()
	{
		return this.bodyAtomArgumentConverter;
	}

	private DroolsSWRLBuiltInArgument2DRLConverter getSWRLBuiltInArgumentConverter()
	{
		return this.builtInArgumentConverter;
	}

	private DroolsOWLPropertyExpressionConverter getOWLPropertyExpressionConverter()
	{
		return this.propertyExpressionConverter;
	}

	private DroolsOWLClassExpressionConverter getOWLClassExpressionConverter()
	{
		return this.classExpressionConverter;
	}

	private String addQuotes(String s)
	{
		return "\"" + s + "\"";
	}
}
