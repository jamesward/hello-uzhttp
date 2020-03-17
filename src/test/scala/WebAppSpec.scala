import zio.test._
import zio.test.Assertion._
import zio.test.environment._

object WebAppSpec extends DefaultRunnableSpec {
  def spec = suite("WebAppSpec")(

    testM("port defaults to 8080") {
      assertM(WebApp.zioPort)(equalTo(8080.toChar))
    },

    // todo: is it possible that a generated string is a valid integer?
    testM("ports must be ints") {
      checkM(Gen.anyString) { s =>
        for {
          _ <- TestSystem.putEnv("PORT", s)
          port <- WebApp.zioPort.run
        } yield {
          assert(port)(fails(isSubtype[WebApp.InvalidPortValue](anything)))
        }
      }
    },

    // todo: maybe better to partition anyInt into valid & invalid values?
    testM("ports must be in the valid char range") {
      checkM(Gen.anyInt) { i =>
        for {
          _ <- TestSystem.putEnv("PORT", i.toString)
          port <- WebApp.zioPort.run
        } yield {
          assert(port) {
            if (i < Char.MinValue || i > Char.MaxValue)
              fails(isSubtype[WebApp.InvalidPortValue](anything))
            else
              succeeds(equalTo(i.toChar))
          }
        }
      }
    },

  )
}
