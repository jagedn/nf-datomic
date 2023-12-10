include { movies } from 'plugin/nf-datomic'

channel
        .from( movies() ) | map { it.toUpperCase() } | view