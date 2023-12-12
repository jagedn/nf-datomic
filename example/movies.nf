
def movies = [
        'the goonies',
        'comando',
        'interstellar'
]

channel
        .from( movies ) | map { it.toUpperCase() } | view