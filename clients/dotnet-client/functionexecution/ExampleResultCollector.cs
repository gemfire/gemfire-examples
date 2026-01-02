// Copyright 2024 Broadcom. All Rights Reserved.

using GemFire.Client;

namespace functionexecution
{
    internal class ExampleResultCollector : IResultCollector
    {
        private readonly List<object> _results = new();

        public void AddResult(object result)
        {
            _results.Add(result);
        }

        public void ClearResults()
        {
            _results.Clear();

        }

        public void EndResults()
        {
            throw new NotImplementedException();
        }

        public List<object> GetResults(TimeSpan timeout)
        {
            return _results;
        }
    }
}
