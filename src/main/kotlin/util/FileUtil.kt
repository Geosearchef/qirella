package util

import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.browser.document

// https://robkendal.co.uk/blog/2020-04-17-saving-text-to-client-side-file-using-vanilla-js
fun downloadFile(data: ByteArray, filename: String) {
    val file = Blob(arrayOf(data), BlobPropertyBag(type = "application/x-binary"))
    val a = document.createElement("a") as HTMLAnchorElement

    with(a) {
        href = URL.createObjectURL(file)
        download = filename
        click()
        URL.revokeObjectURL(href)
    }
}

fun loadFile(fileExtensions: List<String>, callback: (String) -> Unit) {
    val input = document.createElement("input") as HTMLInputElement

    with(input) {
        type = "file"
        accept = fileExtensions.joinToString(",")
        addEventListener("change", {
            input.files?.get(0)?.let { file ->
                FileReader().apply {
                    onload = { callback((result)) } // welcome to js callback hell
                    readAsBinaryString(file)
                }
            }
        }, false)
        click()
    }
}