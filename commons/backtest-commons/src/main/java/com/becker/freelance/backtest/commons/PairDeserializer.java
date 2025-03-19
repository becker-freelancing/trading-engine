package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class PairDeserializer extends JsonDeserializer<Pair> {

    @Override
    public Pair deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.getCodec().readTree(p);
        String technicalName = node.get("technicalName").toString();
        Pair pair = Pair.fromTechnicalName(technicalName.replaceAll("\"", ""));
        return new UnmodifiablePair(pair);
    }

    /**
     * @param delegate Da aktuell alle Pairs im Classpath sind und der Technical Name nicht eindeutig sit muss
     *                 ein BugFix gemacht werden, welcher wirklich das richtige Pair heraussucht.
     *                 <p>
     *                 Aktuell funktioniert da Programm trotzdem noch, da mit dem Pair nichts mehr gemacht wird.
     *                 <p>
     *                 Diese Klasse dient nur zur Sicherheit, falls auf Methoden von Pair zugegriffen wird, wird eine Exception geworfen.
     */
    private record UnmodifiablePair(Pair delegate) implements Pair {

        @Override
        public String baseCurrency() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public String counterCurrency() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal sizeMultiplication() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal leverageFactor() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal profitPerPointForOneContract() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal minOrderSize() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal minStop() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public Decimal minLimit() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public String technicalName() {
            return delegate.technicalName();
        }

        @Override
        public long timeInMinutes() {
            throw new UnsupportedOperationException("BugFix must be done");
        }

        @Override
        public boolean isExecutableInAppMode(AppMode appMode) {
            throw new UnsupportedOperationException("BugFix must be done");
        }
    }
}
