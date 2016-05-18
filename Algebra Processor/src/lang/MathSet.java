package lang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Mathematical Set class which differs from Java's Set class in that this class
 * contains a powerSet method and creates a Set from a String.
 *
 * @author Nikola Istvanic
 */
public class MathSet {
    /**
     * Backing Set.
     */
    private Set<String> set;

    /**
     * Empty MathSet creator.
     */
    public MathSet() {
        set = new HashSet<>();
    }

    /**
     * Constructor for MathSet object which takes a formatted String and
     * converts it into a Set.
     * @param contents String which contains the elements to be contained in the
     * new Set.
     */
    public MathSet(String contents) {
        String braces = contents.replaceAll("[^{}]", "");
        if (!isMatched(braces)) {
            throw new IllegalArgumentException(
                    "A brace is not matched with another.");
        }
        contents = contents.replaceAll("\\s", "").replace("{}", "\u2205");
        if (!Pattern.matches(
                "\u2205|(\\{+([a-zA-Z0-9]|\u2205|\\{)+"
                + "(,([a-zA-Z0-9]|\u2205|\\{+|\\}*)+)*\\}+)", contents)) {
           throw new IllegalArgumentException(
                   "Incorrect formatting. Separate individual elements by "
                   + "commas: {A, B, {C, D}, E}.");
        }
        set = new HashSet<>();
        if (!contents.equals("\u2205")) {
            String[] elements = contents.split(",");
            for (int i = 0; i < elements.length; i++) {
                if (i == 0) {
                    if (Pattern.matches("\\{\\{", elements[i])) {
                        String nestedSet = elements[i].replace("{", "");
                        i++;
                        while (!elements[i].contains("}")) {
                            nestedSet += ", " + elements[i++];
                        }
                        nestedSet += ", " + elements[i].substring(
                                0, elements[i].indexOf("}") + 1);
                        set.add(nestedSet);
                    } else {
                        set.add(elements[i].replaceAll("\\{|\\}", ""));
                    }
                } else {
                    if (Pattern.matches("\\{(.)+", elements[i])) {
                        String nestedSet = elements[i];
                        i++;
                        while (!elements[i].contains("}")) {
                            nestedSet += ", " + elements[i++];
                        }
                        nestedSet += ", " + elements[i].substring(
                                0, elements[i].indexOf("}") + 1);
                        set.add(nestedSet);
                    } else {
                        set.add(elements[i].replaceAll("\\{|\\}", ""));
                    }
                }
            }
        }
    }

    /**
     * Getter for the backing Set of this MathSet.
     * @return The backing Set.
     */
    public Set<String> getSet() {
        return set;
    }

    /**
     * Method which returns the cardinality (size) of this MathSet.
     * @return The size of the backing Set which is the cardinality of the
     * mathematical set.
     */
    public int cardinality() {
        return set.size();
    }

    /**
     * Method that determines whether or not the set entered has braces that
     * match with one another; in other words, for every '{', there is a '}'.
     * @param expression The String form of the Set.
     * @return True if all opening braces match with all closing braces;
     * false otherwise.
     */
    public boolean isMatched(String expression) {
        String opening = "{", closing = "}";
        Stack<Character> buffer = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (opening.indexOf(c) != -1) {
                buffer.push(c);
            } else if (closing.indexOf(c) != -1) {
                if (buffer.isEmpty()) {
                    return false;
                }
                if (closing.indexOf(c) != opening.indexOf(buffer.pop())) {
                    return false;
                }
            }
        }
        return buffer.isEmpty();
    }

    /**
     * Static recursive method that finds the Power Set of an entered Set.
     * @param originalSet Set form of a MathSet (the backing Set).
     * @return A Set of Sets which is the Power Set of the original MathSet.
     */
    public static Set<Set<String>> powerSet(Set<String> originalSet) {
        Set<Set<String>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        List<String> list = new ArrayList<>(originalSet);
        String head = list.get(0);
        Set<String> rest = new HashSet<>(list.subList(1, list.size()));
        for (Set<String> set : powerSet(rest)) {
            Set<String> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    /**
     * Hashcode method which returns the hash of this MathSet.
     * @return The combined hash of the elements of the backing Set.
     */
    @Override
    public int hashCode() {
        int base = 17, hashCode = 0;
        for (String string : set) {
            hashCode += base * string.hashCode();
        }
        return hashCode;
    }

    /**
     * Equals method that determines if two MathSets are equal.
     * @return True if the two are equal; false otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MathSet)) {
            return false;
        }
        if (object == this) {
            return true;
        }
        MathSet other = (MathSet) object;
        return other.getSet().containsAll(getSet())
                && getSet().containsAll(other.getSet());
    }

    /**
     * Method that returns a String representation of the MathSet as well as the
     * MathSet's cardinality and Power Set.
     * @return String representation of the MathSet.
     */
    @Override
    public String toString() {
        if (set.size() == 0) {
            return "\u2205\nCardinality: 0\nPower Set: {\u2205}";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String string : set) {
            sb.append(string + ", ");
        }
        sb.replace(sb.length() - 2, sb.length(), "");
        sb.append("}\nCardinality: " + cardinality() + "\nPower Set: "
            + powerSet(getSet()).toString().replaceAll("\\[\\]", "\u2205")
            .replaceAll("\\[", "{").replaceAll("\\]", "}"));
        return sb.toString();
    }
}
