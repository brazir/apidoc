package core.generator

import core.ServiceDescription
import core.ScalaServiceDescription

import java.io.File
import java.io.PrintWriter

import scala.sys.process._

import org.scalatest.FlatSpec

class Play2ClientGeneratorSpec extends FlatSpec {
  "Play2ClientGenerator" should "generate scala" in {
    val home = sys.props("user.home")
    val json = scala.io.Source.fromFile(s"$home/apidoc/api.json").getLines.mkString("\n")
    val serviceDescription = ServiceDescription(json)
    val ssd = new ScalaServiceDescription(serviceDescription)
    val p2c = new Play2ClientGenerator(ssd)
    val dir = new File("/web/iris-hub-client/src/main/scala/irishub")
    dir.mkdirs
    val pw = new PrintWriter(new File(dir, "Client.scala"))
    try pw.println(p2c.src)
    pw.close()
    val token = sys.props.getOrElse("irishub.api.token", "")
    val url = sys.props.getOrElse("irishub.api.url", "")
    val proc = Process(
      Seq(
        "sbt",
        s"-Dirishub.api.token=${token}",
        s"-Dirishub.api.url=${url}",
        "compile"
      ),
      new File("/web/iris-hub-client")
    )
    assert(proc.! == 0)
  }
}
