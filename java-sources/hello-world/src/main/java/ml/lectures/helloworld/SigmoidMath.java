package ml.lectures.helloworld;

/**
 * SigmoidMath  description
 *
 * @author <a href="mailto:oslautin@luxoft.com">Oleg N.Slautin</a>
 */
public class SigmoidMath implements LearnMath {

    private final double epsilon;
    private final double alpha;

    /**
     * Constructor
     * @param epsilon - learning speed
     * @param alpha - inertia moment
     */
    public SigmoidMath(final double epsilon, final double alpha) {

        this.epsilon = epsilon;
        this.alpha = alpha;
    }

    @Override
    public double deviation(final double actual, final double ideal) {

        return Math.pow(ideal - actual, 2) / 2.;
    }

    @Override
    public double logisticFun(final double x) {

        return 1 / (1 + Math.pow(Math.E, (-1 * x)));
    }

    /**
     * DELTA.w = EPSILON * GRAD.w + ALPHA * DELTA.w-1
     * @param grad - GRAD.w
     * @param delta - previous delta
     * @return deltaw
     */
    @Override
    public double weightDelta(final double grad, final double delta) {

        return epsilon * grad + alpha * delta;
    }

    @Override
    public double gradient(final double out, final double delta) {

        return out * delta;
    }

    /**
     * Delta for neuron:
     * H.delta = F'(IN) * SUM(Wi * OUT.delta)
     * F'(IN) = F.sigmoid = (1 - OUT) * OUT
     * @param out - OUT
     * @param weight - Wi
     * @param delta - OUT.delta
     * @return delta for outputs
     */
    @Override
    public double neuronDelta(final double out, final double weight, final double delta) {

        return (1 - out) * out * (weight * delta);
    }

    /**
     * Delta for outputs:
     * OUT.delta = (OUT.ideal - OUT.actual) * F'(IN)
     * F'(IN) = F.sigmoid = (1 - OUT.actual) * OUT.actual
     * @param actual - OUT.actual
     * @param ideal - OUT.ideal
     * @return delta for outputs
     */
    @Override
    public double outputDelta(final double actual, final double ideal) {

        return (ideal - actual) * (1 - actual) * actual;
    }
}