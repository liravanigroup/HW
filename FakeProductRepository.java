public List<Product> find(String[] tags, String author, Money minPrice, Money maxPrice, boolean acceptNotAvailable) {
        if (emptyFilter(tags, author, minPrice, maxPrice, acceptNotAvailable))
            return new ArrayList<>(fakeDatabase.values());
        return Lists.newLinkedList(Iterables.filter(fakeDatabase.values(), product -> isCorrectAvailavle(acceptNotAvailable, product) && 
                                                                                        isCorrectPrices(minPrice, maxPrice, product)));
    }

    private boolean isCorrectAvailavle(boolean acceptNotAvailable, Product product) {
        return !(acceptNotAvailable || product.isAvailable());
    }

    private boolean isCorrectPrices(Money minPrice, Money maxPrice, Product product) {
        return minPrice != null && product.calculatePrice().isGreaterOrEqualsThan(minPrice) &&
                maxPrice != null && product.calculatePrice().isLessOrEqualsThan(maxPrice);
    }

    private boolean emptyFilter(String[] tags, String author, Money minPrice, Money maxPrice, boolean acceptNotavailable) {
        return !acceptNotavailable &&
                (tags == null || tags.length == 0)
                && author == null
                && minPrice == null && maxPrice == null;
    }
