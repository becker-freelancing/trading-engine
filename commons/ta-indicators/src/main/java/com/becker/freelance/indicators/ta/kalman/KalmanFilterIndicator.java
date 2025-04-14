package com.becker.freelance.indicators.ta.kalman;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.List;

public class KalmanFilterIndicator extends CachableIndicator<Integer, Num> implements Indicator<Num> {


    private final Indicator<Num> estimationIndicator;
    private int lastUpdateIndex;
    private Num estimate;
    private Num errorCovariance;
    private Num processNoise;
    private Num measurementNoise;

    /**
     * Die initiale Fehlerkovarianz gibt an, wie sicher du zu Beginn in deine erste Schätzung bist. Sie beschreibt die Unsicherheit der Schätzung, bevor du die ersten Messungen machst. Je größer der Wert, desto weniger vertraust du auf deine Anfangsschätzung.
     * <p>
     * Heuristik: Setze eine relativ hohe Fehlerkovarianz, wenn du wenig über den Zustand zu Beginn weißt. Ein gängiger Ansatz ist, sie auf einen großen Wert zu setzen (z. B. 1000), um anzunehmen, dass du zu Beginn sehr unsicher bist.
     * <p>
     * <p>
     * <p>
     * <p>
     * Das Prozessrauschen modelliert, wie stark sich der Zustand des Systems von einer Messung zur nächsten ändern kann. Es beschreibt die Unvorhersehbarkeit des Systems und wird oft basierend auf der Dynamik des Systems gesetzt.
     * <p>
     * Heuristik: Ein kleinerer Wert für
     * 𝑄
     * Q bedeutet, dass du davon ausgehst, dass das System relativ stabil ist und sich nur langsam ändert. Ein größerer Wert für
     * 𝑄
     * Q bedeutet, dass du mit größeren Änderungen zwischen den Messungen rechnest.
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * Das Messrauschen beschreibt die Unsicherheit, die mit den Messungen selbst verbunden ist. Es berücksichtigt, wie ungenau die Messungen sind.
     * <p>
     * Heuristik: Wenn du den Quellen der Messungen vertraust (z. B. Börsenkurse oder zuverlässige Sensoren), wird das Messrauschen kleiner sein. Wenn du hingegen auf ungenaue Daten angewiesen bist, wird
     * 𝑅
     * R größer.
     *
     * @param initialErrorCovariance
     * @param initialProcessNoise
     */
    public KalmanFilterIndicator(Indicator<Num> estimationIndicator, Num initialEstimate, Num initialErrorCovariance, Num initialProcessNoise, Num initialMeasurementNoise) {
        super(1000);
        this.estimationIndicator = estimationIndicator;
        this.lastUpdateIndex = -1;
        this.estimate = initialEstimate;
        this.errorCovariance = initialErrorCovariance;
        this.processNoise = initialProcessNoise;
        this.measurementNoise = initialMeasurementNoise;
    }

    /**
     * Berechnung: Wenn du historisch Daten hast und eine bestimmte Unsicherheit über die Anfangsschätzung machen möchtest, kannst du den Standardabweichungswert der ersten Messungen als Fehlerkovarianz verwenden. Zum Beispiel:
     * <p>
     * 𝑃_0 = 𝜎^2
     * wobei
     * 𝜎
     * σ die Standardabweichung der ersten Messungen ist.
     * <p>
     * <p>
     * <p>
     * Berechnung: Wenn du eine Annahme über die Stabilität des Systems treffen kannst, zum Beispiel durch historische Volatilität der Preisbewegungen, kannst du
     * 𝑄
     * Q schätzen:
     * <p>
     * 𝑄 =~ (Varianz der Preisänderung) / T
     * T die Zeit zwischen den Messungen ist.
     * <p>
     * <p>
     * Berechnung: Wenn du historisch gesehen die Messungen und deren Fehler kennst (z. B. durch Vergleich von Börsenkursen mit tatsächlichen Marktwerten), kannst du den Fehlerstandardabstand der Messungen berechnen. Wenn du keine weiteren Informationen hast, kannst du auch einfach den Wert der Standardabweichung der Messungen verwenden:
     * <p>
     * 𝑅=sigma^2_(Messung)
     *
     * @param historicalPrices
     */
    public KalmanFilterIndicator(Indicator<Num> estimationIndicator, Num initialEstimate, List<Num> historicalPrices) {
        this(estimationIndicator, initialEstimate, calcInitialCovariance(historicalPrices), calcInitialProcessNoise(historicalPrices), calcInitialMeasurementNoise(historicalPrices));
    }

    private static Num calcInitialMeasurementNoise(List<Num> historicalPrices) {
    }

    private static Num calcInitialProcessNoise(List<Num> historicalPrices) {
    }

    private static Num calcInitialCovariance(List<Num> historicalPrices) {
    }


    @Override
    public Num getValue(int index) {

        if (lastUpdateIndex == -1) {
            lastUpdateIndex = index;
        }

        if (index > lastUpdateIndex + 1) {
            throw new IllegalStateException("Kalman Filter is not calculatable in future");
        }

        if (index <= lastUpdateIndex) {
            return findInCache(index).orElse(null);
        }

        lastUpdateIndex = index;

        errorCovariance = errorCovariance.plus(processNoise);
        Num kalmanGain = errorCovariance.dividedBy(errorCovariance.plus(measurementNoise));
        Num value = estimationIndicator.getValue(index);
        estimate = estimate.plus(kalmanGain.multipliedBy(value.minus(estimate)));
        errorCovariance = DecimalNum.valueOf(1).minus(kalmanGain).multipliedBy(errorCovariance);

        putInCache(index, estimate);
        return estimate;
    }

    @Override
    public int getUnstableBars() {
        return estimationIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return estimationIndicator.getBarSeries();
    }
}
