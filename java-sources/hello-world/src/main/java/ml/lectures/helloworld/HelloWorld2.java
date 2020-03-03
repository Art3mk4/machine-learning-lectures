package ml.lectures.helloworld;

import lombok.val;

import static java.lang.String.format;
import static java.lang.System.out;

/**
 * HelloWord  description
 *
 * @author <a href="mailto:oslautin@luxoft.com">Oleg N.Slautin</a>
 */
public class HelloWorld2 {

    static final int VERTEX_CNT = 7;
    static final int W_CNT = 9;

    static final double EPSILON = 0.7;
    static final double ALPHA = 0.3;

    static final int w1 = 0;
    static final int w2 = 1;
    static final int w3 = 2;
    static final int w4 = 3;
    static final int w5 = 4;
    static final int w6 = 5;
    static final int w7 = 6;
    static final int w8 = 7;
//    static final int w9 = 8;

    static final int i1 = 0;
    static final int i2 = 1;
    static final int h1 = 2;
    static final int h2 = 3;
    static final int o1 = 4;
    static final int b1 = 5;

    public static void main(String[] args) {

        double[] weights = {0.5, 0.3, -0.5, 0.5, 0.2, 0.3, 0.2, -0.2, 0.1};
        int[][] trainset = {
            {0, 0, 0},
            {0, 1, 1},
            {1, 1, 0},
            {1, 0, 1}
        };
        dumpWeights(weights);
//        val deltas = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for (int e = 0; e < 400000; e++) {
            double error = 0;
            val deltas = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < trainset.length; i++) {
                val outputs = new double[VERTEX_CNT];
                outputs[b1] = 1;
                val ideal = trainset[i][2];
                passForward(trainset[i], weights, outputs);
                error += error(outputs[o1], ideal);
                passBackward(weights, deltas, outputs, deltao(outputs[o1], ideal));
            }
//            passBackward(weights, deltas, outputs, deltao(outputs[o1], ideal));
            for (int i = w1; i <= w8; i++) {
                weights[i] = weights[i] + deltas[i];
            }

            if (e % 10000 == 0) {
                out.println("error: " + error);
                dumpWeights(weights);
            }
        }
        //test weights
        out.println("\nResults");
        for (int i = 0; i < trainset.length; i++) {
            val outputs = new double[VERTEX_CNT];
            outputs[b1] = 1;
            val ideal = trainset[i][2];
            passForward(trainset[i], weights, outputs);
            val error = error(outputs[o1], ideal);
            out.println(format("%d\t%d\t%d\t%.3f\t%.3f",
                trainset[i][0], trainset[i][1], ideal, outputs[o1], error
                )
            );
        }
    }

    private static void passBackward(final double[] weights,
                                     final double[] deltas,
                                     final double[] outputs,
                                     final double deltao) {

        deltas[w5] = deltaw(grad(outputs[h1], deltao), deltas[w5]);
        deltas[w6] = deltaw(grad(outputs[h2], deltao), deltas[w6]);
//        deltas[w9] = deltaw(grad(outputs[b1], deltao), deltas[w9]);

        val deltaH1 = deltah(outputs[h1], weights[w5], deltao);
        val deltaH2 = deltah(outputs[h2], weights[w6], deltao);

        //h1
        deltas[w1] = deltaw(grad(outputs[i1], deltaH1), deltas[w1]);
        deltas[w3] = deltaw(grad(outputs[i2], deltaH1), deltas[w3]);
        deltas[w8] = deltaw(grad(outputs[b1], deltaH1), deltas[w8]);

        //h2
        deltas[w2] = deltaw(grad(outputs[i1], deltaH2), deltas[w2]);
        deltas[w4] = deltaw(grad(outputs[i2], deltaH2), deltas[w4]);
        deltas[w7] = deltaw(grad(outputs[b1], deltaH2), deltas[w7]);
    }

    private static void dumpWeights(final double[] weights) {
        out.println(format("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f",
            weights[w1], weights[w2], weights[w3],
            weights[w4], weights[w5], weights[w6], weights[w7], weights[w8])
        );
    }

    private static void passForward(final int[] set,
                                    final double[] weights,
                                    final double[] outputs) {

        outputs[i1] = set[0];
        outputs[i2] = set[1];
        val inp = new double[5];
        inp[h1] = outputs[i1] * weights[w1] + set[1] * weights[w3];
        inp[h2] = outputs[i1] * weights[w2] + set[1] * weights[w4];

        //H-outputs
        outputs[h1] = sigmoid(inp[h1]);
        outputs[h2] = sigmoid(inp[h2]);

        inp[o1] = outputs[h1] * weights[w5] + outputs[h2] * weights[w6];
        outputs[o1] = sigmoid(inp[o1]);
    }

    /**
     * DELTA.w = EPSILON * GRAD.w + ALPHA * DELTA.w-1
     * @param grad - GRAD.w
     * @param delta - DELTA.w-1
     * @return deltaw
     */
    private static double deltaw(final double grad, final double delta) {

        return EPSILON * grad + ALPHA * delta;
    }

    /**
     * Delta for neuron:
     * GRAD.a.b = DELTA.b * OUT.a
     * @param out - actual output
     * @return delta for outputs
     */
    private static double grad(final double out, final double delta) {

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
    private static double deltah(final double out,
                                 final double weight,
                                 final double delta) {

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
    static double deltao(final double actual, final double ideal) {

        return (ideal - actual) * (1 - actual) * actual;
    }

    static double error(final double actual, final double ideal) {

        return Math.pow(ideal - actual, 2) / 2.;
    }

    static double sigmoid(double x) {

        return 1 / (1 + Math.pow(Math.E, (-1 * x)));
    }
}