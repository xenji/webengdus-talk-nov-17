package webengdus.elasticsearch

data class IndexableDocument(
        val name: String,
        val description: String,
        val price: Double,
        val stock: Int)