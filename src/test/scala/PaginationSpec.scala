package io.prismic

import org.specs2.mutable._

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }

class PaginationSpec extends Specification {

  private def await[A](fua: Future[A]) = Await.result(fua, DurationInt(5).seconds)

  private lazy val api = await(Api.get("https://lesbonneschoses.cdn.prismic.io/api", cache = BuiltInCache()))

  private val ref = api.master.ref
  private def query(page: Int, pageSize: Int = 20) =
    await(api.forms("everything").ref(ref)
      .page(page).pageSize(pageSize).submit())

  private def orderedQuery(page: Int, pageSize: Int = 20) =
    await(api.forms("everything").ref(ref)
      .page(page).pageSize(pageSize).orderings("[my.docchapter.priority]").submit())

  "Pagination" should {
    "first page" in {
      val res = query(1)
      res.page must_== 1
      res.results.head.id must_== "UlfoxUnM0wkXYXbV"
      res.results.size must_== 20
      res.resultsPerPage must_== 20
      res.resultsSize must_== 20
      res.totalPages must_== 2
      res.nextPage must_== Some(s"https://d2aw36oac6sa9o.cloudfront.net/api/documents/search?ref=$ref&page=2&pageSize=20")
      res.prevPage must_== None
    }
    "second page" in {
      val res = query(2)
      res.page must_== 2
      res.results.size must_== 20
      res.resultsPerPage must_== 20
      res.resultsSize must_== 20
      res.totalPages must_== 2
      res.nextPage must_== None
      res.prevPage must_== Some(s"https://d2aw36oac6sa9o.cloudfront.net/api/documents/search?ref=$ref&page=1&pageSize=20")
    }
    "setting page size" in {
      val res = query(1, 7)
      res.page must_== 1
      res.results.size must_== 7
      res.resultsPerPage must_== 7
      res.resultsSize must_== 7
      res.totalPages must_== 6
      res.nextPage must_== Some(s"https://d2aw36oac6sa9o.cloudfront.net/api/documents/search?ref=$ref&page=2&pageSize=7")
      res.prevPage must_== None
    }
  }
}
