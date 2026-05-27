package ram.talia.moreiotas.common.casting.actions.matrices

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import org.ejml.simple.SimpleMatrix
import org.jblas.DoubleMatrix
import ram.talia.moreiotas.MoreIotasConfig
import ram.talia.moreiotas.api.asActionResult
import ram.talia.moreiotas.api.asMatrix
import ram.talia.moreiotas.api.getNumOrVecOrMatrix
import ram.talia.moreiotas.api.matrixWrongSize
import ram.talia.moreiotas.api.mod.MoreIotasConfig

class OpConcatMatrix(private val concatVertically: Boolean) : ConstMediaAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val mat0 = args.getNumOrVecOrMatrix(0, argc).asMatrix
        val mat1 = args.getNumOrVecOrMatrix(1, argc).asMatrix

        if (concatVertically) {
            if (mat0.numCols != mat1.numCols)
                throw MishapInvalidIota.matrixWrongSize(args[1], 0, null, mat0.columns)
            if (mat0.numRows + mat1.numRows > MoreIotasConfig.maxMatrixSize.get())
                throw MishapInvalidIota.of(args[1],
                        0,
                        "matrix.max_size",
                    MoreIotasConfig.maxMatrixSize.get(),
                        mat0.numRows + mat1.numRows,
                        mat0.numCols)
        } else {
            if (mat0.numRows != mat1.numRows)
                throw MishapInvalidIota.matrixWrongSize(args[1], 0, mat0.numRows, null)
            if (mat0.numCols + mat1.numCols > MoreIotasConfig.maxMatrixSize.get())
                throw MishapInvalidIota.of(args[1],
                        0,
                        "matrix.max_size",
                    MoreIotasConfig.maxMatrixSize.get(),
                        mat0.numRows,
                        mat0.numCols + mat1.numCols)
        }

        return if (concatVertically)
            SimpleMatrix
            .concatVertically(mat0, mat1).asActionResult
        else DoubleMatrix.concatHorizontally(mat0, mat1).asActionResult
    }
}