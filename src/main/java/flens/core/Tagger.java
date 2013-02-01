package flens.core;

public interface Tagger {
	

	Tagger empty = new Tagger() {
		
		@Override
		public void adapt(Record r) {
		}
	};

	public void adapt(Record r);

}
