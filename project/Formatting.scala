package example

import sbt._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

object Formatting {
  lazy val formatSettings = Seq(
    ScalariformKeys.preferences in Compile  := formattingPreferences.value,
    ScalariformKeys.preferences in Test     := formattingPreferences.value
  )

  def formattingPreferences = Def.setting {
    import scalariform.formatter.preferences._
    ScalariformKeys.preferences.value
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(DoubleIndentConstructorArguments, false)
  }

}